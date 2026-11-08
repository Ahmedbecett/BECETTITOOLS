package com.becetti.tools.data.local.store

import android.content.Context
import com.becetti.tools.core.utils.LocalStore
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import kotlin.math.roundToLong

/* ------------------------------------------ models ----------------------------------------- */

data class Customer(
    val id: String = "",
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val taxId: String = "",
    val note: String = "",
    val createdAt: Long = 0L
)

data class Product(
    val id: String = "",
    val name: String = "",
    val sku: String = "",
    val barcode: String = "",
    val unitPrice: Double = 0.0,
    val costPrice: Double = 0.0,
    val taxPercent: Double = 0.0,
    val stock: Int = 0,
    val lowStockAt: Int = 5,
    val unit: String = "pc"
) {
    val marginPercent: Double
        get() = if (unitPrice <= 0.0) 0.0 else ((unitPrice - costPrice) / unitPrice) * 100.0
    val lowStock: Boolean get() = stock <= lowStockAt
}

data class LineItem(
    val productId: String = "",
    val description: String = "",
    val qty: Double = 1.0,
    val unitPrice: Double = 0.0,
    val taxPercent: Double = 0.0
) {
    val subtotal: Double get() = qty * unitPrice
    val tax: Double get() = subtotal * taxPercent / 100.0
    val total: Double get() = subtotal + tax
}

data class BusinessDoc(
    val id: String = "",
    val kind: String = KIND_INVOICE,
    val number: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val issuedAt: Long = 0L,
    val dueAt: Long = 0L,
    val items: List<LineItem> = emptyList(),
    val discountPercent: Double = 0.0,
    val notes: String = "",
    val currency: String = "USD",
    val status: String = STATUS_DRAFT,
    val stockApplied: Boolean = false
) {
    val subtotal: Double get() = items.sumOf { it.subtotal }
    val taxTotal: Double get() = items.sumOf { it.tax }
    val discount: Double get() = subtotal * discountPercent / 100.0
    val total: Double get() = subtotal - discount + taxTotal
    val isInvoice: Boolean get() = kind == KIND_INVOICE
    val isPaid: Boolean get() = status == STATUS_PAID

    companion object {
        const val KIND_INVOICE = "invoice"
        const val KIND_QUOTE = "quote"
        const val STATUS_DRAFT = "draft"
        const val STATUS_SENT = "sent"
        const val STATUS_PAID = "paid"
        const val STATUS_ACCEPTED = "accepted"
    }
}

data class Expense(
    val id: String = "",
    val label: String = "",
    val amount: Double = 0.0,
    val category: String = "general",
    val at: Long = 0L
)

/* ------------------------------------------- store ----------------------------------------- */

/**
 * Business toolkit persistence: customers, product catalog, inventory, invoices/quotes and expenses.
 * Totals, margins and stock movements are computed from real stored data — nothing is simulated.
 */
class BusinessStore(context: Context) {

    private val customersStore = LocalStore<List<Customer>>(
        context, "becetti_customers", LocalStore.listType(Customer::class.java), { emptyList() }
    )
    private val productsStore = LocalStore<List<Product>>(
        context, "becetti_products", LocalStore.listType(Product::class.java), { emptyList() }
    )
    private val docsStore = LocalStore<List<BusinessDoc>>(
        context, "becetti_documents", LocalStore.listType(BusinessDoc::class.java), { emptyList() }
    )
    private val expensesStore = LocalStore<List<Expense>>(
        context, "becetti_expenses", LocalStore.listType(Expense::class.java), { emptyList() }
    )

    val customers: StateFlow<List<Customer>> = customersStore.data
    val products: StateFlow<List<Product>> = productsStore.data
    val documents: StateFlow<List<BusinessDoc>> = docsStore.data
    val expenses: StateFlow<List<Expense>> = expensesStore.data

    /* ---------- customers ---------- */

    fun addCustomer(customer: Customer): Customer {
        val stored = customer.copy(id = customer.id.ifBlank { UUID.randomUUID().toString() }, createdAt = System.currentTimeMillis())
        customersStore.mutate { it + stored }
        return stored
    }

    fun updateCustomer(customer: Customer) = customersStore.mutate { list ->
        list.map { if (it.id == customer.id) customer else it }
    }

    fun deleteCustomer(id: String) = customersStore.mutate { list -> list.filterNot { it.id == id } }

    /* ---------- products / inventory ---------- */

    fun addProduct(product: Product): Product {
        val stored = product.copy(id = product.id.ifBlank { UUID.randomUUID().toString() })
        productsStore.mutate { it + stored }
        return stored
    }

    fun updateProduct(product: Product) = productsStore.mutate { list ->
        list.map { if (it.id == product.id) product else it }
    }

    fun deleteProduct(id: String) = productsStore.mutate { list -> list.filterNot { it.id == id } }

    fun adjustStock(productId: String, delta: Int) = productsStore.mutate { list ->
        list.map { if (it.id == productId) it.copy(stock = (it.stock + delta).coerceAtLeast(0)) else it }
    }

    fun lowStockProducts(): List<Product> = productsStore.current.filter { it.lowStock }

    fun inventoryValue(): Double = productsStore.current.sumOf { it.stock * it.costPrice }

    /* ---------- invoices & quotes ---------- */

    fun nextNumber(kind: String): String {
        val prefix = if (kind == BusinessDoc.KIND_QUOTE) "QT" else "INV"
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val count = docsStore.current.count { it.kind == kind } + 1
        return "$prefix-$year-${count.toString().padStart(4, '0')}"
    }

    fun saveDoc(doc: BusinessDoc): BusinessDoc {
        val stored = doc.copy(id = doc.id.ifBlank { UUID.randomUUID().toString() })
        docsStore.mutate { list ->
            if (list.any { it.id == stored.id }) list.map { if (it.id == stored.id) stored else it }
            else list + stored
        }
        return stored
    }

    fun deleteDoc(id: String) = docsStore.mutate { list -> list.filterNot { it.id == id } }

    /**
     * Marks an invoice paid/sent and applies the stock movement exactly once for real invoices.
     */
    fun setDocStatus(id: String, status: String) = docsStore.mutate { list ->
        list.map { doc ->
            if (doc.id != id) return@map doc
            val shouldApplyStock = doc.isInvoice && !doc.stockApplied &&
                (status == BusinessDoc.STATUS_PAID || status == BusinessDoc.STATUS_SENT)
            if (shouldApplyStock) {
                productsStore.mutate { products ->
                    products.map { product ->
                        val sold = doc.items.filter { it.productId == product.id }.sumOf { it.qty }
                        if (sold <= 0.0) product else product.copy(stock = (product.stock - sold.roundToLong()).toInt().coerceAtLeast(0))
                    }
                }
            }
            doc.copy(status = status, stockApplied = doc.stockApplied || shouldApplyStock)
        }
    }

    /* ---------- expenses ---------- */

    fun addExpense(expense: Expense) = expensesStore.mutate {
        it + expense.copy(id = expense.id.ifBlank { UUID.randomUUID().toString() }, at = if (expense.at == 0L) System.currentTimeMillis() else expense.at)
    }

    fun deleteExpense(id: String) = expensesStore.mutate { list -> list.filterNot { it.id == id } }

    fun totalExpenses(): Double = expensesStore.current.sumOf { it.amount }

    /* ---------- reporting ---------- */

    fun paidInvoices(): List<BusinessDoc> = docsStore.current.filter { it.isInvoice && it.isPaid }

    fun revenue(): Double = paidInvoices().sumOf { it.total }

    fun outstanding(): Double = docsStore.current.filter { it.isInvoice && it.status == BusinessDoc.STATUS_SENT }.sumOf { it.total }

    fun grossProfit(): Double = revenue() - totalExpenses()

    fun topProducts(limit: Int = 5): List<Pair<String, Double>> {
        val map = mutableMapOf<String, Double>()
        paidInvoices().forEach { doc ->
            doc.items.forEach { item ->
                val label = item.description.ifBlank { "Item" }
                map[label] = (map[label] ?: 0.0) + item.total
            }
        }
        return map.entries.sortedByDescending { it.value }.take(limit).map { it.key to it.value }
    }

    fun clearAll() {
        customersStore.delete()
        productsStore.delete()
        docsStore.delete()
        expensesStore.delete()
    }
}
