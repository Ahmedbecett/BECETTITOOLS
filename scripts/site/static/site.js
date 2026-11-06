/* BECETTI TOOLS — progressive enhancement only.
   Without JavaScript the page is fully readable and every link works. */
(function () {
  "use strict";

  /* ------------------------------- theme ---------------------------------- */
  var root = document.documentElement;
  var KEY = "becetti-theme";
  var mq = window.matchMedia("(prefers-color-scheme: dark)");
  var ORDER = ["system", "light", "dark"];
  var toggle = document.querySelector("[data-theme-toggle]");
  var state = document.querySelector("[data-theme-state]");

  function labels() {
    if (!toggle) return { system: "System", light: "Light", dark: "Dark" };
    return {
      system: toggle.getAttribute("data-label-system") || "System",
      light: toggle.getAttribute("data-label-light") || "Light",
      dark: toggle.getAttribute("data-label-dark") || "Dark",
    };
  }

  function apply(pref) {
    root.dataset.themePref = pref;
    root.dataset.theme = pref === "system" ? (mq.matches ? "dark" : "light") : pref;
    var L = labels();
    var name = L[pref] || L.system;
    if (state) state.textContent = name;
    if (toggle) {
      toggle.setAttribute("title", name);
      toggle.setAttribute("aria-label", name);
      toggle.setAttribute("data-pref", pref);
    }
  }

  function current() {
    try {
      return localStorage.getItem(KEY) || "system";
    } catch (e) {
      return "system";
    }
  }

  if (toggle) {
    apply(current());
    toggle.addEventListener("click", function () {
      var next = ORDER[(ORDER.indexOf(current()) + 1) % ORDER.length];
      try {
        localStorage.setItem(KEY, next);
      } catch (e) {}
      apply(next);
    });
  }

  if (mq.addEventListener) {
    mq.addEventListener("change", function () {
      if (current() === "system") apply("system");
    });
  }

  /* ----------------------------- copy checksum ----------------------------- */
  document.querySelectorAll("[data-copy]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      var text = btn.getAttribute("data-copy") || "";
      var status = document.querySelector(".copy-status");
      var done = btn.getAttribute("data-copied") || "Copied";

      function announce(ok) {
        if (!status) return;
        status.textContent = ok ? done + ": " + text : text;
      }

      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(
          function () {
            announce(true);
          },
          function () {
            announce(false);
          }
        );
        return;
      }
      // Fallback for browsers without the async clipboard API.
      var range = document.createRange();
      var target = document.getElementById("sha256");
      if (target) {
        range.selectNodeContents(target);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
        try {
          document.execCommand("copy");
          announce(true);
        } catch (e) {
          announce(false);
        }
      }
    });
  });

  /* ------------------- ads: consent-gated third-party tag ------------------ */
  /* The Monetag tag is never written into the HTML. It is injected only after the visitor
     presses "Accept", so a fresh visit loads zero third-party code. Declining is sticky;
     the footer's "Ad settings" control brings the bar back to change the choice. */
  (function () {
    var cfgEl = document.getElementById("becetti-ads");
    if (!cfgEl) return;
    var cfg;
    try {
      cfg = JSON.parse(cfgEl.textContent);
    } catch (e) {
      return;
    }
    var banner = document.getElementById("ads-consent");
    var KEY = cfg.consentKey || "becetti-ads-consent";

    function read() {
      try {
        return localStorage.getItem(KEY);
      } catch (e) {
        return null;
      }
    }
    function write(v) {
      try {
        localStorage.setItem(KEY, v);
      } catch (e) {}
    }
    function clear() {
      try {
        localStorage.removeItem(KEY);
      } catch (e) {}
    }
    function loadTag() {
      if (document.getElementById("monetag-tag")) return;
      var s = document.createElement("script");
      s.id = "monetag-tag";
      s.src = cfg.src;
      s.async = true;
      s.setAttribute("data-zone", cfg.zone);
      s.setAttribute("data-cfasync", "false");
      document.head.appendChild(s);
    }
    function show() {
      if (banner) banner.hidden = false;
    }
    function hide() {
      if (banner) banner.hidden = true;
    }

    document.addEventListener("click", function (event) {
      var el = event.target && event.target.closest ? event.target.closest("[data-consent]") : null;
      if (!el) return;
      var action = el.getAttribute("data-consent");
      if (action === "accept") {
        write("accept");
        hide();
        loadTag();
      } else if (action === "decline") {
        write("decline");
        hide();
      } else if (action === "reset") {
        clear();
        show();
      }
    });

    var choice = read();
    if (choice === "accept") loadTag();
    else if (choice !== "decline") show();
  }());

  /* --------------------------- harden external links ----------------------- */
  /* Every download/download-page link now points at the newest published GitHub release,
     so harden anything that leaves the site instead of one hard-coded host. */
  document.querySelectorAll('a[href^="http"]').forEach(function (a) {
    if (a.hostname && a.hostname !== location.hostname) {
      a.setAttribute("rel", "noopener noreferrer nofollow");
    }
  });
})();
