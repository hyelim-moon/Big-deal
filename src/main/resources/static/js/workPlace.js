function openEditBusinessNameModal() {
    var editModal = new bootstrap.Modal(document.getElementById('editBusinessNameModal'));
    editModal.show();
}

function saveBusinessName() {
    var newBusinessName = document.getElementById('newBusinessName').value;
    document.getElementById('businessName').value = newBusinessName;
    var editModal = bootstrap.Modal.getInstance(document.getElementById('editBusinessNameModal'));
    editModal.hide();
    alert("사업장명이 '" + newBusinessName + "'(으)로 변경되었습니다.");
}

function openEditLocationNameModal() {
    var editModal = new bootstrap.Modal(document.getElementById('editLocationNameModal'));
    editModal.show();
}

function saveLocationName() {
    var newLocationName = document.getElementById('newLocationName').value;
    document.getElementById('locationName').value = newLocationName;
    var editModal = bootstrap.Modal.getInstance(document.getElementById('editLocationNameModal'));
    editModal.hide();
    alert("위치정보가 '" + newLocationName + "'(으)로 변경되었습니다.");
}

function openEditBusinessHoursModal() {
    var editModal = new bootstrap.Modal(document.getElementById('editBusinessHoursModal'));
    editModal.show();
}

function saveBusinessHours() {
    var newBusinessHours = document.getElementById('newBusinessHours').value;
    document.getElementById('businessHours').value = newBusinessHours;
    var editModal = bootstrap.Modal.getInstance(document.getElementById('editBusinessHoursModal'));
    editModal.hide();
    alert("영업시간이 '" + newBusinessHours + "'(으)로 변경되었습니다.");
}

function openEditServicesProvidedModal() {
    var editModal = new bootstrap.Modal(document.getElementById('editServicesProvidedModal'));
    editModal.show();
}

function saveServicesProvided() {
    var newServicesProvided = document.getElementById('newServicesProvided').value;
    document.getElementById('servicesProvided').value = newServicesProvided;
    var editModal = bootstrap.Modal.getInstance(document.getElementById('editServicesProvidedModal'));
    editModal.hide();
    alert("제공 서비스가 '" + newServicesProvided + "'(으)로 변경되었습니다.");
}

function openEditAdditionalInfoModal() {
    var editModal = new bootstrap.Modal(document.getElementById('editAdditionalInfoModal'));
    editModal.show();
}

function saveAdditionalInfo() {
    var newAdditionalInfo = document.getElementById('newAdditionalInfo').value;
    document.getElementById('additionalInfo').value = newAdditionalInfo;
    var editModal = bootstrap.Modal.getInstance(document.getElementById('editAdditionalInfoModal'));
    editModal.hide();
    alert("기타 정보가 '" + newAdditionalInfo + "'(으)로 변경되었습니다.");
}

function saveAllChanges() {
    var businessName = document.getElementById('businessName').value;
    var locationName = document.getElementById('locationName').value;
    var businessCategory = document.getElementById('businessCategory').value;
    var businessHours = document.getElementById('businessHours').value;
    var servicesProvided = document.getElementById('servicesProvided').value;
    var additionalInfo = document.getElementById('additionalInfo').value;
    var fileInput = document.getElementById('formFileMultiple');
    var files = fileInput.files;
    var fileNames = [];

    for (var i = 0; i < files.length; i++) {
        fileNames.push(files[i].name);
    }

    // 여기에서 실제로 저장하는 로직을 구현할 수 있습니다.
    alert("모든 변경사항이 저장되었습니다.\n\n" +
          "사업장명: " + businessName + "\n" +
          "위치정보: " + locationName + "\n" +
          "업종: " + businessCategory + "\n" +
          "영업시간: " + businessHours + "\n" +
          "제공 서비스: " + servicesProvided + "\n" +
          "기타: " + additionalInfo + "\n" +
          "첨부된 파일: " + fileNames.join(", "));

    // surveyModal 모달 닫기
    var surveyModal = bootstrap.Modal.getInstance(document.getElementById('surveyModal'));
    surveyModal.hide();
}
