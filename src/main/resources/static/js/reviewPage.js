function submitReview() {
    // 평점 및 리뷰 내용 가져오기
    var rating = $('input[name="rating"]:checked').val();
    var reviewText = $('#reviewText').val();
    var imageUpload = $('#imageUpload')[0].files[0];

    // 첨부된 이미지를 Base64로 변환하여 저장
    if (imageUpload) {
        var reader = new FileReader();
        reader.onloadend = function() {
            var base64Image = reader.result;
            saveReview(rating, reviewText, base64Image);
        };
        reader.readAsDataURL(imageUpload);
    } else {
        saveReview(rating, reviewText, null);
    }

    // 모달 닫기
    $('#reviewModal').modal('hide');
}

function saveReview(rating, reviewText, base64Image) {
    // 리뷰 객체 생성
    var review = {
        rating: rating,
        reviewText: reviewText,
        image: base64Image
    };

    // 기존 리뷰 목록 가져오기
    var reviews = JSON.parse(localStorage.getItem('reviews')) || [];

    // 새로운 리뷰 추가
    reviews.push(review);

    // 로컬 스토리지에 저장
    localStorage.setItem('reviews', JSON.stringify(reviews));

    // 로그에 출력 (테스트용)
    console.log("리뷰 저장:", review);
}