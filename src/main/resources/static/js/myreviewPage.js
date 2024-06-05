// Sample review data (replace this with actual data from your application)
const reviews = [
    { id: 1, rating: 4, text: "좋았어요", businessName: "가게1", date: "2024-05-30" },
    { id: 2, rating: 5, text: "매우 만족합니다", businessName: "가게2", date: "2024-05-29" },
    // Add more review objects as needed
];

// Function to populate the review list
function populateReviewList() {
    const reviewList = document.getElementById('reviewList');
    reviewList.innerHTML = ''; // Clear existing list

    reviews.forEach(review => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${review.rating}</td>
            <td>${review.businessName}</td>
            <td>${review.date}</td>
            <td>
                <button class="btn btn-outline-primary btn-sm" onclick="enableEdit(${review.id})">수정</button>
                <button class="btn btn-outline-danger btn-sm" onclick="confirmDeleteReview(${review.id})">삭제</button>
            </td>
        `;
        reviewList.appendChild(row);
    });

    // Add event listeners to edit review buttons
    const editReviewButtons = document.querySelectorAll('.edit-review-btn');
    editReviewButtons.forEach(button => {
        button.addEventListener('click', () => {
            const reviewId = parseInt(button.getAttribute('data-review-id'));
            const review = reviews.find(review => review.id === reviewId);
            if (review) {
                populateEditReviewModal(review);
                $('#editReviewModal').modal('show');
            } else {
                alert('리뷰를 찾을 수 없습니다.');
            }
        });
    });
}

// Function to populate the edit review modal with review data
function populateEditReviewModal(review) {
    document.getElementById('editReviewRating').value = review.rating;
    document.getElementById('editReviewText').value = review.text;
    // Additional fields can be populated similarly
}

// Function to handle submitting edited review
document.getElementById('submitEditReviewButton').addEventListener('click', () => {
    // Here you can write code to submit the edited review to the server
    // After successful submission, you may want to update the UI accordingly
    alert('평점이 수정되었습니다.');
    $('#editReviewModal').modal('hide');
    // You may want to reload the review list or update it based on the changes
    populateReviewList();
});

// Function to confirm review deletion
function confirmDeleteReview(reviewId) {
    if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
        deleteReview(reviewId);
    }
}

// Function to delete a review
function deleteReview(reviewId) {
    const index = reviews.findIndex(review => review.id === reviewId);
    if (index !== -1) {
        reviews.splice(index, 1);
        populateReviewList(); // Update the review list after deletion
        alert('리뷰가 삭제되었습니다.');
    } else {
        alert('리뷰를 찾을 수 없습니다.');
    }
}

// Call the populateReviewList function when the page is loaded
window.addEventListener('DOMContentLoaded', () => {
    populateReviewList();
});
