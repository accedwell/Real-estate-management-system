/* REMS - small shared helpers */

/** Hide table rows whose text does not contain the search box value. */
function filterRows(inputId, tbodyId) {
    var q = document.getElementById(inputId).value.toLowerCase().trim();
    var rows = document.getElementById(tbodyId).rows;
    for (var i = 0; i < rows.length; i++) {
        rows[i].style.display = rows[i].textContent.toLowerCase().indexOf(q) !== -1 ? '' : 'none';
    }
}

/** Pre-fill empty date inputs marked data-today with today's date. */
document.addEventListener('DOMContentLoaded', function () {
    var d = new Date();
    var today = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
    document.querySelectorAll('input[type=date][data-today]').forEach(function (el) {
        if (!el.value) el.value = today;
    });
});
