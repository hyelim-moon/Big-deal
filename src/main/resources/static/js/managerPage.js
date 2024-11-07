document.addEventListener('DOMContentLoaded', function() {
    window.navigateTo = function(page) {
        window.location.href = page;
    };

    window.navigateToHome = function() {
        window.location.href = 'index.html';
    };
});
