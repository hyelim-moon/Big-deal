$(document).ready(function() {
    var isUsernameValid = false;
    var isEmailSent = false;
    var isEmailVerified = false;

    function updateRegistrationButtonStatus() {
        if (isUsernameValid && isEmailSent && isEmailVerified) {
            $('#submit-registration').prop('disabled', false);
        } else {
            $('#submit-registration').prop('disabled', true);
        }
    }

    // 아이디 중복 확인
    $("#check-username, #check-business-username").click(function() {
        var usernameInputId = $(this).data('username-input-id');
        var username = $('#' + usernameInputId).val().trim();
        if (username === '') {
            alert('아이디를 입력해주세요.');
            return;
        }

        const requestURL = `/api/v1/member/duplication/username/${encodeURIComponent(username)}`;
        fetch(requestURL, { method: 'GET' })
            .then(response => {
                if (!response.ok) throw new Error(`Server responded with status: ${response.status}`);
                return response.json();
            })
            .then(isDuplicate => {
                if (isDuplicate) {
                    alert('이미 사용 중인 아이디입니다.');
                    isUsernameValid = false;
                } else {
                    alert('사용 가능한 아이디입니다.');
                    isUsernameValid = true;
                }
                updateRegistrationButtonStatus();
            })
            .catch(error => {
                console.error('아이디 중복 검사 중 오류 발생:', error);
                alert('아이디 중복 검사 중 오류가 발생했습니다.');
                isUsernameValid = false;
                updateRegistrationButtonStatus();
            });
    });

    // 이메일 발송 요청
    $("#send-email").click(function() {
        const email = $('#email').val().trim();
        if (!email) {
            alert('이메일을 입력해주세요.');
            return;
        }
        sendEmail(email);
    });

    async function sendEmail(email) {
        try {
            const response = await fetch('/api/v1/member/auth/email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email })
            });
            if (response.ok) {
                alert('인증 코드가 이메일로 발송되었습니다.');
                isEmailSent = true;
            } else {
                const errorData = await response.json();
                alert('이메일 발송에 실패하였습니다: ' + errorData.message);
                isEmailSent = false;
            }
            updateRegistrationButtonStatus();
        } catch (error) {
            alert('이메일 발송 중 오류가 발생했습니다.');
            isEmailSent = false;
            updateRegistrationButtonStatus();
        }
    }

    // 인증번호 확인
    $("#verify-authCode").click(function() {
        const email = $('#email').val().trim();
        const authCode = $('#authCode').val().trim();
        verifyAuthCode(email, authCode);
    });

    async function verifyAuthCode(email, authCode) {
        try {
            const response = await fetch('/api/v1/member/auth/code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email, code: authCode })
            });
            const data = await response.json();
            if (response.ok) {
                alert('인증 성공, 회원가입을 계속 진행하세요.');
                if (data.accessToken) {
                    localStorage.setItem('authToken', data.accessToken);
                    console.log('인증 토큰:', data.accessToken);
                    isEmailVerified = true;
                } else {
                    console.error('토큰이 응답에 포함되지 않았습니다.');
                    isEmailVerified = false;
                }
            } else {
                alert('인증 실패');
                isEmailVerified = false;
            }
            updateRegistrationButtonStatus();
        } catch (error) {
            alert('인증번호 검증 중 오류가 발생했습니다.');
            isEmailVerified = false;
            updateRegistrationButtonStatus();
        }
    }

    // 폼 제출 이벤트
    $('#user-form').on('submit', function(event) {
        event.preventDefault();
        if (!isUsernameValid || !isEmailSent || !isEmailVerified) {
            alert('모든 필드를 올바르게 완료해주세요.');
            return;
        }
        const formData = {
            username: $('#username').val(),
            password: $('#password').val(),
            email: $('#email').val()
        };
        submitRegistration(formData);
    });

    async function submitRegistration(formData) {
        if (!localStorage.getItem('authToken')) {
            alert('로그인을 먼저 진행해주세요.');
            return;
        }
        try {
            const token = localStorage.getItem('authToken'); // 저장된 인증 토큰 사용
            const response = await fetch('/api/v1/member', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            // 응답 처리
            if (response.ok) {
                const data = await response.json();
                console.log('회원가입 성공');
                console.log('memberUuid:', data.uuid); // uuid 확인
                localStorage.setItem('memberUuid', data.uuid); // uuid 저장
                alert('회원가입 성공');
                window.location.href = 'loginPage.html'; // 성공 페이지로 리디렉션
            } else {
                const errorData = await response.json();
                console.error('회원가입 실패:', errorData.message || '알 수 없는 오류');
                alert(`회원가입 실패: ${errorData.message || '알 수 없는 오류'}`);
            }
        } catch (error) {
            console.error('회원가입 요청 중 오류 발생:', error);
            alert('회원가입 요청 중 오류가 발생했습니다.');
        }
    }

    updateRegistrationButtonStatus(); // 초기 상태 업데이트
});
