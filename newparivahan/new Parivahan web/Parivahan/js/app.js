// default map layer
// let map = L.map('map', {
//     layers: MQ.mapLayer(),
//     center: [35.791188, -78.636755],
//     zoom: 12
// });
    // let marker = MQ.marker(latLng).addTo()    
    // marker = L.marker(latLng).addTo(map);
    // L.marker([35.791188, -78.636755]).addTo(map)

    function addMap(lat,lng) {
       
        let map = L.map('map', {
            layers: MQ.mapLayer(),
            center: [lat,lng],
            zoom: 12
        });
        // var map = L.map('map').setView([lat,lng], 13);
        L.marker([lat, lng]).addTo(map)
        console.log("myletlng"+lat+lng);
    }
    function runDirection(start, end) {
        
        // recreating new map layer after removal
        // map = L.map('map', {
        //     layers: MQ.mapLayer(),
        //     center: [35.791188, -78.636755],
        //     zoom: 12
        // });
        
        var dir = MQ.routing.directions();

        dir.route({
            locations: [
                start,
                end
            ]
        });
    
        
        CustomRouteLayer = MQ.Routing.RouteLayer.extend({
            createStartMarker: (location) => {
                var custom_icon;
                var marker;

                custom_icon = L.icon({
                    iconUrl: 'img/red.png',
                    iconSize: [20, 29],
                    iconAnchor: [10, 29],
                    popupAnchor: [0, -29]
                });

                marker = L.marker(location.latLng, {icon: custom_icon}).addTo(map);

                return marker;
            },

            createEndMarker: (location) => {
                var custom_icon;
                var marker;

                custom_icon = L.icon({
                    iconUrl: 'img/blue.png',
                    iconSize: [20, 29],
                    iconAnchor: [10, 29],
                    popupAnchor: [0, -29]
                });

                marker = L.marker(location.latLng, {icon: custom_icon}).addTo(map);

                return marker;
            }
        });
        
        map.addLayer(new CustomRouteLayer({
            directions: dir,
            fitBounds: true
        })); 
    }


// function that runs when form submitted
function submitForm(event) {
    event.preventDefault();

    // delete current map layer
    map.remove();

    // getting form data
    start = document.getElementById("start").value;
    end = document.getElementById("destination").value;

    // run directions function
    runDirection(start, end);

    // reset form
    document.getElementById("form").reset();
}

// asign the form to form variable
const form = document.getElementById('form');

// call the submitForm() function when submitting the form
form.addEventListener('submit', submitForm);


//******************************* * This is for live locaiton


console.log('this is my log');
if (!navigator.geolocation) {
    console.log("your Brouser Doesn`t support geolocaion feature!");
}else{
    // navigator.geolocation.getCurrentPosition((postion)=>{
        // console.log(`lat:${postion.coords.latitude},lng:${postion.coords.longitude}`);
    // });
    let desLat = 0;
    let deslng = 0;
    let map;
    let ela = false;
    let elamap = false;
    let id =  navigator.geolocation.watchPosition((postion)=>{
    console.log(`lat:${postion.coords.latitude},lng:${postion.coords.longitude}`);  
    // if (ela == true) {
    //     map.remove();
    //     elamap = true;
    // }
    // if (elamap = true) {
    //     ela = false;
    var accuracy = postion.coords.accuracy;
        map = L.map('map', {
            layers: MQ.mapLayer(),
            center: [postion.coords.latitude,postion.coords.longitude],
            zoom: 15
        });   
        L.marker([postion.coords.latitude, postion.coords.longitude]).addTo(map)
        L.cr
        elamap = false;
        // }
        // ela = true;
    // addMap(postion.coords.latitude,postion.coords.longitude);
    if (postion.coords.latitude === desLat) {
        navigator.geolocation.clearWatch(id);
    }
    },(err)=>{
        console.log(err);
    },
    {
        enableHighAccuracy:true,
        timeout:5000,
        maximumAge:0
    }); 
};