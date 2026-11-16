/* REMS - app shell extras: menus, notifications, quick search, sortable tables, back-to-top */
(function () {
    'use strict';

    function store(key, value) {
        try {
            if (value === undefined) return JSON.parse(localStorage.getItem(key) || 'null');
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) { return null; }
    }

    /* ------------------------------------------------------------ dropdown menus */
    var pops = document.querySelectorAll('.pop');
    function closePops(except) {
        pops.forEach(function (p) { if (p !== except) p.classList.remove('open'); });
    }
    pops.forEach(function (pop) {
        var btn = pop.querySelector('[data-pop]');
        if (!btn) return;
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            var opening = !pop.classList.contains('open');
            closePops(pop);
            pop.classList.toggle('open', opening);
            btn.setAttribute('aria-expanded', opening ? 'true' : 'false');
            if (opening && pop.id === 'notes') markNotesSeen();
        });
    });
    document.addEventListener('click', function (e) {
        if (!e.target.closest('.pop-menu')) closePops();
    });

    /* ------------------------------------------------------------ notifications: unread state */
    var SEEN = 'rems.seenNotes';
    var notes = document.querySelectorAll('#notes .note-item');
    var dot = document.querySelector('#notes .bell-dot');
    var seen = store(SEEN) || [];
    var unread = 0;
    notes.forEach(function (n) {
        if (seen.indexOf(n.dataset.key) === -1) { n.classList.add('unread'); unread++; }
    });
    if (dot) {
        if (unread === 0) dot.hidden = true; else dot.textContent = unread;
    }
    function markNotesSeen() {
        var keys = [];
        notes.forEach(function (n) { keys.push(n.dataset.key); });
        store(SEEN, keys);
        if (dot) dot.hidden = true;
    }

    /* ------------------------------------------------------------ quick search (Ctrl/Cmd + K) */
    var items = [];
    function add(label, href, icon, group) {
        if (!href || href === '#') return;
        for (var i = 0; i < items.length; i++) if (items[i].href === href) return;
        items.push({ label: label, href: href, icon: icon || '', group: group });
    }
    document.querySelectorAll('.sidebar .side-link').forEach(function (a) {
        var svg = a.querySelector('svg');
        add(a.textContent.replace(/\d+$/, '').trim(), a.getAttribute('href'), svg ? svg.outerHTML : '', 'Pages');
    });
    document.querySelectorAll('.app-topbar .topbar-right > a.btn, .dash-hero-actions a, .qa-list a.qa').forEach(function (a) {
        var svg = a.querySelector('svg');
        add(a.textContent.trim(), a.getAttribute('href'), svg ? svg.outerHTML : '', 'Actions');
    });
    document.querySelectorAll('.user-menu a').forEach(function (a) {
        var svg = a.querySelector('svg');
        add(a.textContent.replace('Ctrl K', '').trim(), a.getAttribute('href'), svg ? svg.outerHTML : '', 'Account');
    });

    var modal = null, input, list, active = 0, shown = [];
    function build() {
        modal = document.createElement('div');
        modal.className = 'cmd';
        modal.innerHTML =
            '<div class="cmd-box" role="dialog" aria-modal="true" aria-label="Quick search">' +
            '<div class="cmd-input"><svg class="i" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>' +
            '<input type="text" placeholder="Type a page or action&hellip;" aria-label="Search pages and actions"><kbd>Esc</kbd></div>' +
            '<div class="cmd-list" role="listbox"></div>' +
            '<div class="cmd-foot"><span><kbd>&uarr;</kbd><kbd>&darr;</kbd> to move</span><span><kbd>Enter</kbd> to open</span><span><kbd>Esc</kbd> to close</span></div>' +
            '</div>';
        document.body.appendChild(modal);
        input = modal.querySelector('input');
        list = modal.querySelector('.cmd-list');
        modal.addEventListener('click', function (e) { if (e.target === modal) closeCmd(); });
        input.addEventListener('input', function () { active = 0; render(); });
        input.addEventListener('keydown', function (e) {
            if (e.key === 'ArrowDown') { e.preventDefault(); active = Math.min(active + 1, shown.length - 1); render(); }
            else if (e.key === 'ArrowUp') { e.preventDefault(); active = Math.max(active - 1, 0); render(); }
            else if (e.key === 'Enter' && shown[active]) { location.href = shown[active].href; }
            else if (e.key === 'Escape') { closeCmd(); }
        });
    }
    function esc(t) {
        return String(t).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
    function render() {
        var q = input.value.toLowerCase().trim();
        shown = items.filter(function (it) {
            if (!q) return true;
            var hay = (it.label + ' ' + it.group).toLowerCase();
            return q.split(/\s+/).every(function (w) { return hay.indexOf(w) !== -1; });
        });
        if (!shown.length) {
            list.innerHTML = '<p class="cmd-empty">No matches for &ldquo;' + esc(input.value) + '&rdquo;</p>';
            return;
        }
        var html = '', group = '';
        shown.forEach(function (it, i) {
            if (it.group !== group) { group = it.group; html += '<p class="cmd-group">' + group + '</p>'; }
            html += '<a class="cmd-item' + (i === active ? ' active' : '') + '" href="' + it.href + '" data-i="' + i + '">' +
                '<span class="cmd-ico">' + it.icon + '</span><span>' + esc(it.label) + '</span>' +
                '<svg class="i go" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 4v7a4 4 0 0 1-4 4H4"/><path d="m9 10-5 5 5 5"/></svg></a>';
        });
        list.innerHTML = html;
        var cur = list.querySelector('.cmd-item.active');
        if (cur) cur.scrollIntoView({ block: 'nearest' });
        list.querySelectorAll('.cmd-item').forEach(function (a) {
            a.addEventListener('mousemove', function () {
                var i = +a.dataset.i;
                if (i !== active) { active = i; render(); }
            });
        });
    }
    function openCmd() {
        if (!modal) build();
        closePops();
        modal.classList.add('open');
        input.value = '';
        active = 0;
        render();
        setTimeout(function () { input.focus(); }, 10);
    }
    function closeCmd() { if (modal) modal.classList.remove('open'); }
    document.querySelectorAll('[data-cmd-open]').forEach(function (b) {
        b.addEventListener('click', function (e) { e.preventDefault(); openCmd(); });
    });
    document.addEventListener('keydown', function (e) {
        if ((e.ctrlKey || e.metaKey) && (e.key === 'k' || e.key === 'K')) { e.preventDefault(); openCmd(); }
        else if (e.key === 'Escape') { closeCmd(); closePops(); }
    });

    /* ------------------------------------------------------------ sortable tables */
    function cellValue(td) {
        var t = (td ? td.textContent : '').trim();
        var num = t.replace(/[R\s,]/g, '');
        if (/^-?\d+(\.\d+)?%?$/.test(num)) return { n: parseFloat(num) };
        if (/^\d{4}-\d{2}-\d{2}/.test(t)) return { n: Date.parse(t.slice(0, 10)) };
        return { s: t.toLowerCase() };
    }
    document.querySelectorAll('table.data').forEach(function (table) {
        var body = table.tBodies[0];
        if (!body || body.rows.length < 2) return;
        var heads = table.tHead ? table.tHead.rows[0].cells : [];
        Array.prototype.forEach.call(heads, function (th, col) {
            if (!th.textContent.trim()) return;
            th.classList.add('sortable');
            th.setAttribute('tabindex', '0');
            th.title = 'Sort by ' + th.textContent.trim().toLowerCase();
            function sort() {
                var dir = th.getAttribute('aria-sort') === 'ascending' ? -1 : 1;
                Array.prototype.forEach.call(heads, function (h) { h.removeAttribute('aria-sort'); });
                th.setAttribute('aria-sort', dir === 1 ? 'ascending' : 'descending');
                var rows = Array.prototype.slice.call(body.rows).filter(function (r) { return r.cells.length > col; });
                rows.sort(function (a, b) {
                    var x = cellValue(a.cells[col]), y = cellValue(b.cells[col]);
                    if (x.n !== undefined && y.n !== undefined) return (x.n - y.n) * dir;
                    return String(x.s !== undefined ? x.s : x.n).localeCompare(String(y.s !== undefined ? y.s : y.n)) * dir;
                });
                rows.forEach(function (r) { body.appendChild(r); });
            }
            th.addEventListener('click', sort);
            th.addEventListener('keydown', function (e) { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); sort(); } });
        });
    });

    /* ------------------------------------------------------------ dismissible messages */
    document.querySelectorAll('.app-main .alert').forEach(function (al) {
        if (al.style.display === 'none') return;
        var x = document.createElement('button');
        x.type = 'button';
        x.className = 'alert-x';
        x.setAttribute('aria-label', 'Dismiss message');
        x.innerHTML = '<svg class="i" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>';
        x.addEventListener('click', function () {
            al.classList.add('leaving');
            setTimeout(function () { al.remove(); }, 200);
        });
        al.appendChild(x);
    });

    /* ------------------------------------------------------------ back to top */
    var top = document.querySelector('.to-top');
    if (top) {
        top.addEventListener('click', function () { window.scrollTo({ top: 0, behavior: 'smooth' }); });
        var onScroll = function () { top.classList.toggle('show', window.scrollY > 500); };
        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll();
    }
})();
