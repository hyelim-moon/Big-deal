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

    // 로그인 함수
    async function login(username, password) {
        const loginData = {
            username: username,
            password: password
        };

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
        return data.accessToken; // 반환된 토큰
    }

    // 일반 회원 가입 폼 제출 처리
    userForm.addEventListener('submit', async function(event) {
        event.preventDefault(); // 폼 제출 기본 동작 막기

        const formData = {
            username: userForm.username.value,
            password: userForm.password.value,
            email: userForm.email.value
        };

        const response = await fetch('http://localhost:8080/api/v1/member', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            console.log('회원가입 성공');
            // 회원가입 성공 후 로그인 페이지로 리디렉션
            window.location.href = 'loginpage.html';
        } else {
            console.error('회원가입 실패');
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

        const response = await fetch('http://localhost:8080/api/v1/member', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            console.log('회원가입 성공');
            // 회원가입 성공 후 로그인 페이지로 리디렉션
            window.location.href = 'loginpage.html';
        } else {
            console.error('회원가입 실패');
        }
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
});
