function selectRating(rating) {
    // 별점 선택 시 동작할 코드를 여기에 추가하세요.
}

function submitReview() {
    // 여기에 평점 등록 로직을 추가하세요.
    // 선택된 별점 값과 리뷰 내용을 가져와서 처리하는 코드를 작성합니다.
    // 예를 들어, 서버로 전송하거나 저장하는 등의 작업을 수행합니다.
    // 이 코드는 실제로 데이터를 처리하는 부분이므로 프로젝트에 맞게 수정하셔야 합니다.

    // 모달 닫기
    $('#reviewModal').modal('hide');

    // 평점 및 리뷰 내용 가져오기
    var rating = $('input[name="rating"]:checked').val();
    var reviewText = $('#reviewText').val();

    // 가져온 데이터를 로그에 출력 (테스트용)
    console.log("별점: " + rating);
    console.log("리뷰 내용: " + reviewText);
}