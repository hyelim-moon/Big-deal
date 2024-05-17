$(document).ready(function(){
    // 사업자 버튼 클릭 시 사업자 가입 폼 표시
    $("#business-btn").click(function(){
        $("#user-form").hide();
        $("#business-form").show();
    });

    // 일반 회원 버튼 클릭 시 일반 회원 가입 폼 표시
    $("#user-btn").click(function(){
        $("#business-form").hide();
        $("#user-form").show();
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const userForm = document.getElementById('user-form');
    const businessForm = document.getElementById('business-form');

// 일반 회원 가입 폼 제출 처리
userForm.addEventListener('submit', async function(event) {
    event.preventDefault(); // 폼 제출 기본 동작 막기

    const formData = {
        username: userForm.username.value,
        password: userForm.password.value,
        email: userForm.email.value
    };

    try {
        const response = await fetch('http://localhost:8080/api/v1/member', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            console.log('회원가입 성공');
            alert('회원가입 성공');
            // 회원가입 성공 후 로그인 페이지로 리디렉션
            window.location.href = 'loginPage.html';
        } else {
            const errorData = await response.json();
            console.error('회원가입 실패:', errorData.message);
            alert(`회원가입 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('회원가입 요청 중 오류 발생:', error);
        alert('회원가입 요청 중 오류가 발생했습니다.');
    }
});

// 사업자 회원 가입 폼 제출 처리
businessForm.addEventListener('submit', async function(event) {
    event.preventDefault(); // 폼 제출 기본 동작 막기

    const formData = {
        username: businessForm['business-username'].value,
        password: businessForm['business-password'].value,
        email: businessForm['business-email'].value
    };

    try {
        const response = await fetch('http://localhost:8080/api/v1/member', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            console.log('회원가입 성공');
            alert('회원가입 성공');
            // 회원가입 성공 후 로그인 페이지로 리디렉션
            window.location.href = 'loginPage.html';
        } else {
            const errorData = await response.json();
            console.error('회원가입 실패:', errorData.message);
            alert(`회원가입 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('회원가입 요청 중 오류 발생:', error);
        alert('회원가입 요청 중 오류가 발생했습니다.');
    }
});

 // 일반 회원 아이디 중복 확인  -- 아이디 중복확인 엔드포인트 필요
    $("#check-username").click(async function() {
        const username = $("#username").val();

        if (!username) {
            alert('아이디를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/v1/member/check-username?username=${username}`);
            if (response.ok) {
                const data = await response.json();
                if (data.available) {
                    alert(`${username} 는 사용 가능한 아이디입니다.`);
                } else {
                    alert(`${username} 는 이미 사용 중인 아이디입니다.`);
                }
            } else {
                alert('아이디 중복 확인 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('아이디 중복 확인 요청 중 오류 발생:', error);
            alert('아이디 중복 확인 요청 중 오류가 발생했습니다.');
        }
    });

    // 사업자 아이디 중복 확인
    $("#check-business-username").click(async function() {
        const username = $("#business-username").val();

        if (!username) {
            alert('아이디를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/v1/member/check-username?username=${username}`);
            if (response.ok) {
                const data = await response.json();
                if (data.available) {
                    alert(`${username} 는 사용 가능한 아이디입니다.`);
                } else {
                    alert(`${username} 는 이미 사용 중인 아이디입니다.`);
                }
            } else {
                alert('아이디 중복 확인 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('아이디 중복 확인 요청 중 오류 발생:', error);
            alert('아이디 중복 확인 요청 중 오류가 발생했습니다.');
        }
    });
});


// 사업자 및 일반 회원 선택 버튼 클릭 이벤트 처리
document.getElementById('business-btn').addEventListener('click', function() {
    userForm.style.display = 'none';
    businessForm.style.display = 'block';
});

document.getElementById('user-btn').addEventListener('click', function() {
    userForm.style.display = 'block';
    businessForm.style.display = 'none';
});

