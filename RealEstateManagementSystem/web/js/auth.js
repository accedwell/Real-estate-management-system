/* REMS - login / sign-up page behaviour */
(function () {
    // Show / hide password
    document.querySelectorAll('.pw-toggle').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var input = document.getElementById(btn.dataset.target);
            var show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            btn.classList.toggle('on', show);
            btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
            btn.title = show ? 'Hide password' : 'Show password';
            input.focus();
        });
    });

    // Caps Lock warning on the login password
    var caps = document.getElementById('caps');
    var pw = document.getElementById('password');
    if (caps && pw) {
        ['keyup', 'keydown'].forEach(function (ev) {
            pw.addEventListener(ev, function (e) {
                if (e.getModifierState) caps.hidden = !e.getModifierState('CapsLock');
            });
        });
    }

    // Password strength meter + match check (sign-up)
    var meter = document.getElementById('meter');
    var meterText = document.getElementById('meter-text');
    var confirm = document.getElementById('confirm');
    var matchText = document.getElementById('match-text');
    var labels = ['Mix letters, numbers & symbols.', 'Weak password', 'Fair password', 'Good password', 'Strong password'];

    function score(v) {
        if (!v) return 0;
        var s = 0;
        if (v.length >= 6) s++;
        if (v.length >= 10) s++;
        if (/[a-z]/.test(v) && /[A-Z]/.test(v)) s++;
        if (/\d/.test(v) && /[^A-Za-z0-9]/.test(v)) s++;
        else if (/\d/.test(v) || /[^A-Za-z0-9]/.test(v)) s += 0.5;
        return Math.max(1, Math.min(4, Math.round(s)));
    }
    function checkMatch() {
        if (!confirm || !matchText) return true;
        if (!confirm.value) { matchText.textContent = ''; matchText.className = 'pw-hint'; return false; }
        var ok = confirm.value === pw.value;
        matchText.textContent = ok ? 'Passwords match' : 'Passwords do not match';
        matchText.className = 'pw-hint ' + (ok ? 'ok' : 'bad');
        return ok;
    }
    if (meter && pw) {
        pw.addEventListener('input', function () {
            var s = score(pw.value);
            meter.dataset.score = s;
            meterText.textContent = labels[s];
            checkMatch();
        });
        confirm.addEventListener('input', checkMatch);
    }

    // Validate sign-up before sending, then show a loading state on submit
    document.querySelectorAll('form.auth2-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            var err = document.getElementById('form-error');
            if (form.id === 'signup') {
                var msg = '';
                if (!form.checkValidity()) {
                    var bad = form.querySelector(':invalid');
                    msg = bad && bad.id === 'phone' ? 'Please enter a valid phone number.'
                        : bad && bad.type === 'email' ? 'Please enter a valid email address.'
                        : bad && bad.id === 'password' ? 'Password must be at least 6 characters.'
                        : 'Please fill in all the fields.';
                    if (bad) bad.focus();
                } else if (!checkMatch()) {
                    msg = 'The two passwords do not match.';
                    confirm.focus();
                }
                if (msg) {
                    e.preventDefault();
                    err.textContent = msg;
                    err.hidden = false;
                    return;
                }
                err.hidden = true;
            }
            var btn = form.querySelector('.auth2-submit');
            if (btn) { btn.classList.add('loading'); btn.disabled = true; }
        });
    });
})();
