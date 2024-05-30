document.addEventListener('DOMContentLoaded', function() {
    loadBusinessList();
});
const businessList = [
    // 예시 데이터
    { id: 1, name: "지역화폐가맹점", userId: "user123", category: "음식점", location: "서울" },
    // 더 많은 사업장 데이터를 여기에 추가
];

function loadBusinessList() {
    const businessListElement = document.getElementById('businessList');
    businessListElement.innerHTML = '';

    businessList.forEach((business, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <th scope="row">${index + 1}</th>
            <td>${business.name}</td>
            <td>${business.userId}</td>
            <td>${business.category}</td>
            <td>${business.location}</td>
            <td>
                <button class="btn btn-outline-primary btn-sm" onclick="viewBusinessDetail(${business.id})">수정</button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteBusiness(${business.id})">삭제</button>
            </td>
        `;
        businessListElement.appendChild(row);
    });
}

function viewBusinessDetail(id) {
    const business = businessList.find(b => b.id === id);
    if (!business) return;

    document.getElementById('detailBusinessName').value = business.name;
    document.getElementById('detailBusinessLocation').value = business.location;
    document.getElementById('detailBusinessCategory').value = business.category;
    document.getElementById('detailBusinessHours').value = business.hours;
    document.getElementById('detailServicesProvided').value = business.services;
    document.getElementById('detailAdditionalInfo').value = business.additionalInfo;

    const businessDetailModal = new bootstrap.Modal(document.getElementById('businessDetailModal'));
    businessDetailModal.show();
}

function saveAllChanges() {
    alert('사업장 정보가 저장되었습니다.');
}

function deleteBusiness(id) {
    const index = businessList.findIndex(b => b.id === id);
    if (index !== -1) {
        businessList.splice(index, 1);
        loadBusinessList();
        alert(`사업장 (ID: ${id})이 삭제되었습니다.`);
    }
}

function addNewBusiness() {
    alert('새로운 사업장을 추가합니다.');
}

function enableEdit(elementId) {
    const element = document.getElementById(elementId);
    element.readOnly = false;
    element.focus();
}

function filterBusinesses() {
    const searchName = document.getElementById('searchName').value.toLowerCase();
    const searchID = document.getElementById('searchID').value.toLowerCase();
    const searchCategory = document.getElementById('searchCategory').value.toLowerCase();

    const filteredBusinesses = businessList.filter(business => {
        const nameMatch = business.name.toLowerCase().includes(searchName);
        const idMatch = business.userId.toLowerCase().includes(searchID);
        const categoryMatch = business.category.toLowerCase().includes(searchCategory);
        return nameMatch && idMatch && categoryMatch;
    });

    const businessListElement = document.getElementById('businessList');
    businessListElement.innerHTML = '';

    filteredBusinesses.forEach((business, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <th scope="row">${index + 1}</th>
            <td>${business.name}</td>
            <td>${business.userId}</td>
            <td>${business.category}</td>
            <td>${business.location}</td>
            <td>
                <button class="btn btn-outline-primary btn-sm" onclick="viewBusinessDetail(${business.id})">수정</button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteBusiness(${business.id})">삭제</button>
            </td>
        `;
        businessListElement.appendChild(row);
    });
}