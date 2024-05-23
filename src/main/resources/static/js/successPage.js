document.addEventListener('DOMContentLoaded', function() {
        // 로컬 스토리지에서 아이디 가져오기
        const username = localStorage.getItem('username');
        if (username) {
            document.getElementById('user-id').innerText = username;
        } else {
            document.getElementById('user-id').innerText = '사용자';
        }
    });


