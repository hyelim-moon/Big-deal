document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');

    if (loginForm) {
        loginForm.addEventListener('submit', async function(event) {
            event.preventDefault(); // 폼 제출 기본 동작 막기

            const loginData = {
                username: loginForm.username.value,
                password: loginForm.password.value
            };

            try {
                const response = await fetch('http://localhost:8080/api/v1/member/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(loginData)
                });

                if (!response.ok) {
                    throw new Error('Login failed');
                }

                const data = await response.json();
                console.log('로그인 성공:', data.accessToken);
                alert('로그인 성공');

                // 로그인 성공 시 로컬 스토리지에 아이디 저장
                localStorage.setItem('username', loginData.username);
                localStorage.setItem('accessToken', data.accessToken);

                // 로그인 성공 후, successPage로 리디렉션
                window.location.href = 'successPage.html'; // 실제 보호된 페이지로 변경 필요

            } catch (error) {
                console.error('로그인 실패:', error);
                alert('로그인 실패');
            }
        });
    } else {
        console.error('loginForm is null');
    }
});
