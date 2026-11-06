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
    /* One <script> per configured tag, each guarded by its own id, so a second call to
       loadTag() can never duplicate a network request. */
    function loadTag(report) {
      var tags = cfg.tags || (cfg.src ? [{ src: cfg.src, zone: cfg.zone }] : []);
      var root = document.body || document.documentElement;
      tags.forEach(function (tag) {
        if (!tag || !tag.src) return;
        var id = tag.zone ? "monetag-tag-" + tag.zone : "monetag-tag";
        if (document.getElementById(id)) return;
        var s = document.createElement("script");
        s.id = id;
        s.src = tag.src;
        s.async = true;
        if (tag.zone) s.setAttribute("data-zone", tag.zone);
        s.setAttribute("data-cfasync", "false");
        if (report) {
          s.addEventListener("load", function () {
            report("tag executed: " + tag.src);
          });
          s.addEventListener("error", function () {
            report("tag could NOT be fetched: " + tag.src + " (blocked, offline, or DNS failure)");
          });
        }
        root.appendChild(s);
      });
      document.documentElement.setAttribute("data-ads", "loaded");
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

    /* Ads are always-on by default (cfg.auto): the tag loads on every page view, exactly like a
       snippet pasted into the template, and no banner exists. /ads-verify/ (cfg.unconditional)
       uses the same path plus a plain-language log, so "no ads" can be told apart from "tag never
       ran". Removing auto/requireConsent brings the consent gate back with no code change here. */
    if (cfg.auto === true || cfg.unconditional === true) {
      hide();
      var log = document.getElementById("ads-log");
      var t0 = Date.now();
      function line(text) {
        if (!log) return;
        var li = document.createElement("li");
        li.textContent = ((Date.now() - t0) / 1000).toFixed(1) + "s — " + text;
        log.appendChild(li);
      }
      var addedByTag = 0;
      if (window.MutationObserver) {
        var observer = new MutationObserver(function (records) {
          records.forEach(function (r) {
            addedByTag += r.addedNodes ? r.addedNodes.length : 0;
          });
        });
        observer.observe(document.documentElement, { childList: true, subtree: true });
      }
      line("configuration read: zone(s) " + (cfg.tags || [{ zone: cfg.zone }]).map(function (t) {
        return t.zone;
      }).join(", "));
      loadTag(line);
      line("script element inserted, waiting for the network");
      window.addEventListener("error", function (event) {
        if (event && event.message) line("page error: " + event.message);
      }, true);
      setTimeout(function () {
        if (!log) return;
        if (observer) observer.disconnect();
        if (addedByTag > 0) {
          line("the tag added " + addedByTag + " element(s) to this page");
        } else {
          line("20s later the tag added nothing: it loaded, but this zone returned no ad");
        }
        line("scripts on this page: " + document.querySelectorAll("script[src]").length +
          ", iframes: " + document.querySelectorAll("iframe").length);
      }, 20000);
      return;
    }

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
