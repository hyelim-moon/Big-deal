// 로그아웃시 로컬 스토리지에 있는 토큰을 제거함
// 하지만 토큰이 서버 측에서 여전히 유효할 수 있기 때문에 보안에 취약함
document.addEventListener('DOMContentLoaded', function() {
    const logoutButton = document.getElementById('logout-button');

    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            localStorage.removeItem('username');
            localStorage.removeItem('accessToken');
            alert('로그아웃 성공');
            window.location.href = 'newmapPage.html';
        });
    } else {
        console.error('logoutButton is null');
    }
});


