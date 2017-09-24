function initMap(latitude, longitude) {
    var loc = new google.maps.LatLng(latitude, longitude);
    var zoomMag;
    if (latitude === 0 && longitude === 0)
        zoomMag = 1;
    else
        zoomMag = 14;
    var options = {
        center: loc,
        zoom: zoomMag,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("map"), options);
}
