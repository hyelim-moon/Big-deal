var placeOverlay = new kakao.maps.CustomOverlay({zIndex: 1}),
    contentNode = document.createElement('div'),
    markers = [],
    currCategory = '',
    map = null;  // 전역으로 map 선언

// 장소 검색 객체 생성
var ps = new kakao.maps.services.Places();

// 커스텀 오버레이 내용 설정
contentNode.className = 'placeinfo_wrap';
addEventHandle(contentNode, 'mousedown', kakao.maps.event.preventMap);
addEventHandle(contentNode, 'touchstart', kakao.maps.event.preventMap);
placeOverlay.setContent(contentNode);

// 카테고리 클릭 이벤트 등록
addCategoryClickEvent();

// 이벤트 핸들러 등록 함수
function addEventHandle(target, type, callback) {
    if (target.addEventListener) {
        target.addEventListener(type, callback);
    } else {
        target.attachEvent('on' + type, callback);
    }
}

// HTML5로 사용자의 현재 위치를 가져와 지도의 중심 설정
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

        var locPosition = new kakao.maps.LatLng(lat, lon); // 현재 위치
        var mapOption = {
            center: locPosition, // 현재 위치를 중심으로 지도 설정
            level: 5 // 확대 레벨
        };

        // 지도 생성
        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

        // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
        kakao.maps.event.addListener(map, 'idle', searchPlaces);

    }, function(error) {
        console.error("Error Code = " + error.code + " - " + error.message);
        // 사용자의 위치를 못 가져왔을 때의 기본 지도 설정 (서울시청 중심)
        var mapOption = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청 좌표
            level: 5 // 확대 레벨
        };

        map = new kakao.maps.Map(document.getElementById('map'), mapOption);

        // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
        kakao.maps.event.addListener(map, 'idle', searchPlaces);
    });
} else {
    // 사용자의 위치를 지원하지 않는 경우 기본 위치 설정 (서울시청)
    var mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청 좌표
        level: 3 // 확대 레벨
    };

    map = new kakao.maps.Map(document.getElementById('map'), mapOption);

    // idle 이벤트 등록 (지도가 움직일 때마다 검색 실행)
    kakao.maps.event.addListener(map, 'idle', searchPlaces);
}

// 카테고리 검색 요청 함수
function searchPlaces() {
    if (!currCategory || !map) return;  // map이 초기화되기 전에는 실행되지 않도록 처리

    placeOverlay.setMap(null);  // 커스텀 오버레이 숨기기
    removeMarker();             // 기존 마커 삭제

    ps.categorySearch(currCategory, placesSearchCB, {useMapBounds: true});
}

// 장소 검색 완료 콜백 함수
function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.OK) {
        displayPlaces(data);  // 검색된 장소에 마커 표시
    }
}

// 장소 마커 표시 함수
function displayPlaces(places) {
    for (var i = 0; i < places.length; i++) {
        var marker = addMarker(new kakao.maps.LatLng(places[i].y, places[i].x), i);

        // 마커 클릭 시 장소 정보 표시
        (function(marker, place) {
            kakao.maps.event.addListener(marker, 'click', function() {
                displayPlaceInfo(place);
            });
        })(marker, places[i]);
    }
}

// 마커 추가 함수
function addMarker(position, index) {
    var marker = new kakao.maps.Marker({
        position: position,
        map: map
    });

    markers.push(marker);
    return marker;
}

// 기존 마커 삭제 함수
function removeMarker() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 장소 정보 커스텀 오버레이에 표시
function displayPlaceInfo(place) {
    var content = '<div class="placeinfo">' +
                    '<a href="' + place.place_url + '" target="_blank">' + place.place_name + '</a>' +
                    '<p>' + place.address_name + '</p>' +
                    '<p>' + place.phone + '</p>' +
                  '</div>';

    contentNode.innerHTML = content;
    placeOverlay.setPosition(new kakao.maps.LatLng(place.y, place.x));
    placeOverlay.setMap(map);
}

// 카테고리 클릭 이벤트 등록
function addCategoryClickEvent() {
    var category = document.getElementById('category'),
        children = category.children;

    for (var i = 0; i < children.length; i++) {
        children[i].onclick = onClickCategory;
    }
}

// 카테고리 클릭 시 동작하는 함수
function onClickCategory() {
    var id = this.id,
        className = this.className;

    placeOverlay.setMap(null);

    if (className === 'on') {
        currCategory = '';
        changeCategoryClass();
        removeMarker();
    } else {
        currCategory = id;
        changeCategoryClass(this);
        searchPlaces();
    }
}

// 선택된 카테고리 스타일 변경 함수
function changeCategoryClass(el) {
    var category = document.getElementById('category'),
        children = category.children;

    for (var i = 0; i < children.length; i++) {
        children[i].className = '';
    }

    if (el) el.className = 'on';
}
