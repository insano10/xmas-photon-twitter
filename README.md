# xmas-photon-twitter

This project will search twitter for Christmasy tweets within the London M25 (cos, you know, if it's outside the M25 it may as well be on the other side of the world) 
and sent the location of them to a Particle Photon via a cloud function. The Photon should be hooked up to some kind of awesome circuit involving flashing lights,
sounds and other cool stuff to see which part of London wins in a Christmas tweet-off.


#### Instructions

1. Copy xmas-twitter.properties.template to xmas-twitter.properties (under resources) and fill with your personal data
2. Flash xmas.cpp onto your Particle Photon
3. Run XmasTwitter.java
4. Open the photon serial monitor to see tweet locations being received
5. Build circuit...
