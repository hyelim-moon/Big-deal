// 회원 탈퇴 사유들을 경고창에 표시
function showReasons() {
        let reasons = [];
        let checkboxes = document.querySelectorAll('input[name="surveyOption"]:checked');
        checkboxes.forEach((checkbox) => {
            let label = checkbox.nextElementSibling.textContent;
            if (checkbox.id === 'option4') {
                label += ': ' + document.getElementById('additionalComments').value;
            }
            reasons.push(label);
        });
        alert('탈퇴 사유: ' + reasons.join(', '));
    }

// 로컬 스토리지에서 JWT 토큰을 가져와 요청 헤더에 Authorization 헤더를 추가하여 JWT 토큰을 포함시킴
// 응답이 성공적이면 탈퇴 성공 메시지를 표시하고, 로컬 스토리지에서 사용자 정보를 삭제한 후 리디렉션합니다.
// 응답이 실패하면 오류 메시지를 표시함.
// 회원 탈퇴
function withdraw() {
        showReasons(); // 탈퇴 사유를 먼저 표시

        let reasons = [];
        let checkboxes = document.querySelectorAll('input[name="surveyOption"]:checked');
        checkboxes.forEach((checkbox) => {
            let label = checkbox.nextElementSibling.textContent;
            if (checkbox.id === 'option4') {
                label += ': ' + document.getElementById('additionalComments').value;
            }
            reasons.push(label);
        });

        // JWT 토큰 가져오기
        const token = localStorage.getItem('accessToken');

        if (!token) {
            alert('로그인 상태가 아닙니다.');
            return;
        }

        fetch('http://localhost:8080/api/v1/member', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.ok) {
                alert('회원 탈퇴가 성공적으로 처리되었습니다.');
                localStorage.removeItem('username');
                localStorage.removeItem('accessToken');
                window.location.href = 'mapPage.html'; // 탈퇴 후 리디렉션
            } else {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || '회원 탈퇴 중 오류가 발생했습니다.');
                });
            }
        })
        .catch(error => {
            console.error('회원 탈퇴 요청 중 오류 발생:', error);
            alert('회원 탈퇴 요청 중 오류가 발생했습니다.');
        });
    }