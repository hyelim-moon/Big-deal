var mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(37.5518911, 126.9917937), // 지도의 중심 좌표
        level: 11 // 지도의 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
var markers = []; // 마커를 저장할 배열
var infowindow = new kakao.maps.InfoWindow({removable: true}); // 전역 인포윈도우 객체

// 마커 클러스터러 생성
var clusterer = new kakao.maps.MarkerClusterer({
    map: map,
    averageCenter: true,
    minLevel: 10
});

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

// 지정된 위경도에 위치하는 가맹점들의 위치를 가져와 마커를 생성하고 인포윈도우 표시
function loadData() {
    // 특정한 위도와 경도 범위 지정
        var fromLa = 37.0;
        var toLa = 38.0;
        var fromLo = 126.0;
        var toLo = 128.0;

        // URL 동적 생성
        var url = `/franchise?fromLa=${fromLa}&toLa=${toLa}&fromLo=${fromLo}&toLo=${toLo}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            var localMarkers = data.map(function (franchise) {
                var markerPosition = new kakao.maps.LatLng(franchise.latitude, franchise.longitude);
                var marker = new kakao.maps.Marker({
                    position: markerPosition
                });

                // 클릭 이벤트 리스너 설정
                kakao.maps.event.addListener(marker, 'click', function() {
                    var content = '<div style="padding:5px; white-space: nowrap;">' +
                                  '<strong>상호명:</strong> ' + name + '<br>' +
                                  '<strong>업종:</strong> ' + sector + '<br>' +
                                  '<strong>주소:</strong> ' + map_address + '</div>';
                    infowindow.setContent(content);
                    infowindow.open(map, marker);
                });

                return marker;
            });

            clusterer.addMarkers(localMarkers);
            markers = markers.concat(localMarkers);
        })
        .catch(err => console.error('Error loading the franchise data:', err));
}

// 키워드 검색을 요청하는 함수
function searchPlaces() {
    var keyword = document.getElementById('keyword').value;
    /*
    var region = document.getElementById('region').value;
    var category = document.getElementById('sectors').value;
    var moneytype = document.getElementById('moneytype').value;
    */

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청
    /*ps.keywordSearch(keyword, region, category, moneytype, placesSearchCB);*/
    ps.keywordSearch(keyword, placesSearchCB);
}

// 장소검색이 완료됐을 때 호출되는 콜백함수
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        displayPlaces(data);
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
    } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 결과 중 오류가 발생했습니다.');
    }
}

// 검색 결과 목록과 마커를 표출하는 함수
function displayPlaces(places) {
    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds();

    removeAllChildNods(listEl);
    removeMarker();

    console.log("Displaying places:", places);  // 데이터 검사를 위한 로그

    places.forEach(function(place, index) {
        console.log("Place data:", place);  // 각 장소 데이터 로그
        var markerPosition = new kakao.maps.LatLng(place.y, place.x);
        var marker = new kakao.maps.Marker({
            position: markerPosition
        });
        markers.push(marker);

        var itemEl = getListItem(index, place); // 아이템 생성
        fragment.appendChild(itemEl);
        bounds.extend(markerPosition); // 마커의 위치를 bounds에 추가

        // 이벤트 리스너 설정
        kakao.maps.event.addListener(marker, 'mouseover', function() {
            infowindow.setContent('<div style="padding:5px; white-space: nowrap;">' + place.place_name + '</div>');
            infowindow.open(map, marker);
        });

        kakao.maps.event.addListener(marker, 'mouseout', function() {
            infowindow.close();
        });

        itemEl.onmouseover = function() {
            infowindow.setContent('<div style="padding:5px; white-space: nowrap;">' + place.place_name + '</div>');
            infowindow.open(map, marker);
        };

        itemEl.onmouseout = function() {
            infowindow.close();
        };
    });

    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;
    map.setBounds(bounds); // 모든 마커를 포함하는 범위로 지도 범위 조정
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, place) {
    var el = document.createElement('li'),
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
              '<div class="info">' +
              '<h5>' + place.place_name + '</h5>' +  // 데이터 경로 확인 후 수정
              '<span>' + place.address_name + '</span>' +  // 주소 표시 추가
              '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}

window.onload = loadData; // 페이지 로드 시 데이터 로드 함수 호출