var mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(37.566826004661, 126.978652258309), // 초기 지도의 중심좌표
        level: 12 // 지도의 초기 확대 레벨
    };

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성

// 마커 클러스터러 생성
var clusterer = new kakao.maps.MarkerClusterer({
    map: map,
    averageCenter: true,
    minLevel: 10
});

// 지도 이벤트 리스너 등록, 지도의 범위가 변경되었을 때 실행됩니다 --> 지도 로딩이 완료된 후 한 번만 데이터를 로드하도록 변경.
kakao.maps.event.addListener(map, 'tilesloaded', function() {
    loadMarkers(); // 지도의 보이는 영역이 변경될 때마다 마커를 로드
});

function loadMarkers() {
    // 지도의 현재 영역을 얻습니다.
    var bounds = map.getBounds();
    var swLatLng = bounds.getSouthWest(); // 남서쪽 좌표
    var neLatLng = bounds.getNorthEast(); // 북동쪽 좌표

    var url = `/franchise?fromLa=${swLatLng.getLat()}&toLa=${neLatLng.getLat()}&fromLo=${swLatLng.getLng()}&toLo=${neLatLng.getLng()}`;
                  fetch(url)
        .then(response => response.json())
        .then(data => {
            var markers = data.map(function (franchise) {
                var markerPosition = new kakao.maps.LatLng(franchise.latitude, franchise.longitude);
                var marker = new kakao.maps.Marker({
                    position: markerPosition
                });

                var infowindow = new kakao.maps.InfoWindow({
                    content: '<div style="padding:50px;">' +
                             '상호명: ' + franchise.name + '<br>' +
                             '업종: ' + franchise.sector + '<br>' +
                             '주소: ' + franchise.mapAddress + '</div>',
                    removable: true
                });

                kakao.maps.event.addListener(marker, 'click', function() {
                    infowindow.open(map, marker);
                });

                return marker;
            });
            clusterer.clear(); // 이전 마커들을 클리어
            clusterer.addMarkers(markers); // 새로운 마커들을 클러스터러에 추가
        })
        .catch(err => console.error('Error loading the franchise data:', err));
}
