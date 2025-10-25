
    /*<![CDATA[*/
    const address = [[${selectedList.location}]] + ', ' + [[${selectedList.country}]];
    /*]]>*/

    fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(address)}&format=json`)
      .then(res => res.json())
      .then(data => {
        if (data && data.length > 0) {
          const lat = data[0].lat;
          const lon = data[0].lon;

          const map = L.map('map').setView([lat, lon], 13);

          L.tileLayer(`https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=rq4SvmsD98tIU0oLTLLC`, {
            tileSize: 512,
            zoomOffset: -1,
            attribution: '&copy; <a href="https://www.maptiler.com/">MapTiler</a>'
          }).addTo(map);

          L.marker([lat, lon]).addTo(map)
            .bindPopup(address)
            .openPopup();

          // Fix potential resize issues
          setTimeout(() => map.invalidateSize(), 300);
        } else {
          document.getElementById('map').innerHTML =
            "<p style='color:red;text-align:center'>Map not found for this location.</p>";
        }
      })
      .catch(err => {
        console.error(err);
        document.getElementById('map').innerHTML =
          "<p style='color:red;text-align:center'>Error loading map.</p>";
      });