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






