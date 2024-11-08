document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');

    //일반 로그인 폼 처리
    if (loginForm) {
        loginForm.addEventListener('submit', async function(event) {
            event.preventDefault(); // 폼 제출 기본 동작 막기

            const loginData = {
                username: loginForm.username.value,
                password: loginForm.password.value
            };

            try {
                const response = await fetch('/api/v1/member/auth/login', {
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

                // 로그인 성공 시 로컬 스토리지에 아이디와 accessToken 저장
                localStorage.setItem('username', loginData.username);
                localStorage.setItem('accessToken', data.accessToken);

                // 로그인 성공 후, memberUuid를 가져오기 위해 추가 API 호출
                const uuidResponse = await fetch('/api/v1/member', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + data.accessToken
                    }
                });

                if (!uuidResponse.ok) {
                    throw new Error('Failed to fetch member UUID');
                }

                const memberData = await uuidResponse.json();
                console.log('memberData:', memberData); // 전체 응답 확인

                // 배열의 마지막 요소의 uuid 가져오기
                const memberUuid = memberData[memberData.length - 1].uuid;
                console.log('memberUuid:', memberUuid); // memberUuid 확인
                localStorage.setItem('memberUuid', memberUuid); // memberUuid 저장

                // 로그인 성공 후, newmapPage로 리디렉션
                window.location.href = 'newmapPage.html'; // 실제 보호된 페이지로 변경 필요

            } catch (error) {
                console.error('로그인 실패:', error);
                alert('로그인 실패');
            }
        });
    } else {
        console.error('loginForm is null');
    }

    // 카카오 소셜 로그인 처리
    Kakao.init('a5ea1ed9bba9add0206c13bb610063ae');  // 카카오 앱 키 설정

    // 커스터마이징 버튼 클릭 시 로그인 처리
    document.getElementById('kakao-login-btn').addEventListener('click', function () {
        Kakao.Auth.login({
            success: function (authObj) {
                console.log('카카오 로그인 성공:', authObj);

                // 카카오 로그인 후 사용자 정보 가져오기
                Kakao.API.request({
                    url: '/v2/user/me',
                    success: function (response) {
                        console.log('사용자 정보:', response);

                        // 사용자 정보로 필요한 작업 (예: 프로필 이미지 가져오기)
                        const userProfileImage = response.properties.profile_image;

                        // 로그인 성공 후 버튼 이미지 변경
                        const loginButton = document.getElementById('loginButton');
                        if (loginButton) {
                            loginButton.src = '../img/btn_pro.png'; // 로그인 버튼 이미지를 프로필 이미지로 변경
                        }

                        // 추가적으로 사용자 정보를 로컬 스토리지에 저장하거나 처리할 수 있음
                        localStorage.setItem('userProfile', JSON.stringify(response));

                        // 로그인 성공 후 다른 페이지로 리디렉션
                        window.location.href = 'newmapPage.html';
                    },
                    fail: function (error) {
                        console.error('사용자 정보 요청 실패:', error);
                    }
                });
            },
            fail: function (error) {
                console.error('카카오 로그인 실패:', error);
            }
        });
    });
});
