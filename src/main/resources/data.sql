DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES;
DROP TABLE IF EXISTS SPRING_SESSION;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS listings;


-- Create Session table
CREATE TABLE SPRING_SESSION (
  PRIMARY_ID CHAR(36) NOT NULL,
  SESSION_ID CHAR(36) NOT NULL,
  CREATION_TIME BIGINT NOT NULL,
  LAST_ACCESS_TIME BIGINT NOT NULL,
  MAX_INACTIVE_INTERVAL INT NOT NULL,
  EXPIRY_TIME BIGINT NOT NULL,
  PRINCIPAL_NAME VARCHAR(100),
  CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
  SESSION_PRIMARY_ID CHAR(36) NOT NULL,
  ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
  ATTRIBUTE_BYTES BYTEA NOT NULL,
  CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
  CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);


-- Create listings table
CREATE TABLE listings (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    images_url TEXT DEFAULT 'https://images.unsplash.com/photo-1578645510447-e20b4311e3ce?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDF8fGNhbXBpbmd8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=800&q=60',
    price NUMERIC(10,2) NOT NULL,
    location VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP
);


CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    comment TEXT NOT NULL,
    reviews INT CHECK (reviews BETWEEN 1 AND 5),
    listings_id INT REFERENCES listings(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP
);

-- ✅ Insert listings (with image URLs inline)
INSERT INTO listings (title, description, images_url, price, location, country) VALUES
('Cozy Beachfront Cottage', 'Escape to this charming beachfront cottage for a relaxing getaway.', 'https://images.unsplash.com/photo-1552733407-5d5c46c3bb3b?auto=format&fit=crop&w=800&q=60', 1500, 'Malibu', 'United States'),
('Modern Loft in Downtown', 'Stay in the heart of the city in this stylish loft apartment.', 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=60', 1200, 'New York City', 'United States'),
('Mountain Retreat', 'Unplug and unwind in this peaceful mountain cabin.', 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=60', 1000, 'Aspen', 'United States'),
('Historic Villa in Tuscany', 'Experience the charm of Tuscany in this beautifully restored villa.', 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=60', 2500, 'Florence', 'Italy'),
('Secluded Treehouse Getaway', 'Live among the treetops in this unique treehouse retreat.', 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=60', 800, 'Portland', 'United States'),
('Beachfront Paradise', 'Step out of your door onto the sandy beach.', 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=60', 2000, 'Cancun', 'Mexico'),
('Rustic Cabin by the Lake', 'Spend your days fishing and kayaking on the serene lake.', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=60', 900, 'Lake Tahoe', 'United States'),
('Luxury Penthouse with City Views', 'Indulge in luxury living with panoramic city views.', 'https://images.unsplash.com/photo-1622396481328-9b1b78cdd9fd?auto=format&fit=crop&w=800&q=60', 3500, 'Los Angeles', 'United States'),
('Ski-In/Ski-Out Chalet', 'Hit the slopes right from your doorstep.', 'https://images.unsplash.com/photo-1502784444187-359ac186c5bb?auto=format&fit=crop&w=800&q=60', 3000, 'Verbier', 'Switzerland'),
('Safari Lodge in the Serengeti', 'Experience the thrill of the wild in a comfortable safari lodge.', 'https://images.unsplash.com/photo-1493246507139-91e8fad9978e?auto=format&fit=crop&w=800&q=60', 4000, 'Serengeti National Park', 'Tanzania'),
('Historic Canal House', 'Stay in a piece of history in Amsterdam’s canal district.', 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&w=800&q=60', 1800, 'Amsterdam', 'Netherlands'),
('Private Island Retreat', 'Have an entire island to yourself.', 'https://images.unsplash.com/photo-1618140052121-39fc6db33972?auto=format&fit=crop&w=800&q=60', 10000, 'Fiji', 'Fiji'),
('Charming Cottage in the Cotswolds', 'Escape to the picturesque Cotswolds.', 'https://images.unsplash.com/photo-1602088113235-229c19758e9f?auto=format&fit=crop&w=800&q=60', 1200, 'Cotswolds', 'United Kingdom'),
('Historic Brownstone in Boston', 'Step back in time in this elegant brownstone.', 'https://images.unsplash.com/photo-1533619239233-6280475a633a?auto=format&fit=crop&w=800&q=60', 2200, 'Boston', 'United States'),
('Beachfront Bungalow in Bali', 'Relax on the sandy shores of Bali.', 'https://images.unsplash.com/photo-1602391833977-358a52198938?auto=format&fit=crop&w=800&q=60', 1800, 'Bali', 'Indonesia'),
('Mountain View Cabin in Banff', 'Enjoy breathtaking mountain views from this cozy cabin.', 'https://images.unsplash.com/photo-1521401830884-6c03c1c87ebb?auto=format&fit=crop&w=800&q=60', 1500, 'Banff', 'Canada'),
('Art Deco Apartment in Miami', 'Step into the glamour of the 1920s.', 'https://plus.unsplash.com/premium_photo-1670963964797-942df1804579?auto=format&fit=crop&w=800&q=60', 1600, 'Miami', 'United States'),
('Tropical Villa in Phuket', 'Escape to a tropical paradise in Phuket.', 'https://images.unsplash.com/photo-1470165301023-58dab8118cc9?auto=format&fit=crop&w=800&q=60', 3000, 'Phuket', 'Thailand'),
('Historic Castle in Scotland', 'Live like royalty in this historic castle.', 'https://images.unsplash.com/photo-1585543805890-6051f7829f98?auto=format&fit=crop&w=800&q=60', 4000, 'Scottish Highlands', 'United Kingdom'),
('Desert Oasis in Dubai', 'Experience luxury in the middle of the desert.', 'https://images.unsplash.com/photo-1518684079-3c830dcef090?auto=format&fit=crop&w=800&q=60', 5000, 'Dubai', 'United Arab Emirates'),
('Rustic Log Cabin in Montana', 'Unplug and unwind in this cozy log cabin.', 'https://images.unsplash.com/photo-1586375300773-8384e3e4916f?auto=format&fit=crop&w=800&q=60', 1100, 'Montana', 'United States'),
('Beachfront Villa in Greece', 'Enjoy crystal-clear waters of the Mediterranean.', 'https://images.unsplash.com/photo-1602343168117-bb8ffe3e2e9f?auto=format&fit=crop&w=800&q=60', 2500, 'Mykonos', 'Greece'),
('Eco-Friendly Treehouse Retreat', 'Stay in an eco-friendly treehouse.', 'https://images.unsplash.com/photo-1488462237308-ecaa28b729d7?auto=format&fit=crop&w=800&q=60', 750, 'Costa Rica', 'Costa Rica'),
('Historic Cottage in Charleston', 'Experience the charm of historic Charleston.', 'https://images.unsplash.com/photo-1587381420270-3e1a5b9e6904?auto=format&fit=crop&w=800&q=60', 1600, 'Charleston', 'United States'),
('Modern Apartment in Tokyo', 'Explore vibrant Tokyo from this modern apartment.', 'https://images.unsplash.com/photo-1480796927426-f609979314bd?auto=format&fit=crop&w=800&q=60', 2000, 'Tokyo', 'Japan'),
('Lakefront Cabin in New Hampshire', 'Spend your days by the lake in this cozy cabin.', 'https://images.unsplash.com/photo-1578645510447-e20b4311e3ce?auto=format&fit=crop&w=800&q=60', 1200, 'New Hampshire', 'United States'),
('Luxury Villa in the Maldives', 'Indulge in luxury in this overwater villa.', 'https://images.unsplash.com/photo-1439066615861-d1af74d74000?auto=format&fit=crop&w=800&q=60', 6000, 'Maldives', 'Maldives'),
('Ski Chalet in Aspen', 'Hit the slopes in style with this ski chalet.', 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=800&q=60', 4000, 'Aspen', 'United States'),
('Secluded Beach House in Costa Rica', 'Escape to a secluded beach house on the Pacific coast.', 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&w=800&q=60', 1800, 'Costa Rica', 'Costa Rica'),
('Modern Loft in New York', 'Spacious loft in downtown Manhattan.', 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=60', 3200, 'New York', 'United States');



-- Sample reviews for Listings
INSERT INTO reviews (comment, reviews, listings_id) VALUES
('Beautiful location and cozy stay. Highly recommend!', 5, 1),
('The view was breathtaking, but the WiFi was weak.', 4, 1),
('Perfect for a weekend getaway.', 5, 1),

('Loved the modern vibe and proximity to restaurants.', 5, 2),
('Great location but noisy at night.', 3, 2),
('Stylish and comfortable apartment.', 4, 2),

('Peaceful and clean cabin. Would visit again.', 5, 3),
('A bit hard to find, but worth it.', 4, 3),

('Amazing villa with authentic Tuscan charm.', 5, 4),
('Owner was very hospitable.', 5, 4),
('WiFi could be better.', 3, 4),

('A magical stay among the trees!', 5, 5),
('Fun experience, but lots of bugs.', 3, 5),

('Beachfront view was incredible.', 5, 6),
('Too expensive for the size.', 3, 6),

('Loved kayaking every morning.', 4, 7),
('Cabin was rustic but clean.', 4, 7),

('Luxury at its finest. Stunning city views.', 5, 8),
('A bit pricey but worth it.', 4, 8),

('Skiing straight from the chalet was awesome.', 5, 9),
('Perfect for families.', 5, 9),

('Once-in-a-lifetime safari experience.', 5, 10),
('Unforgettable wildlife!', 5, 10),
('Meals were just average.', 3, 10),

('Classic Amsterdam charm.', 5, 11),
('Can be noisy near the canals.', 4, 11),

('Private island = total paradise.', 5, 12),
('Expensive but magical.', 4, 12),

('Charming and cozy cottage.', 4, 13),
('Loved the fireplace!', 5, 13),

('Historic vibes with modern comfort.', 5, 14),
('The stairs were steep.', 3, 14),

('Beachfront bungalow with stunning views.', 5, 15),
('Staff was kind and helpful.', 5, 15),

('Great mountain views, very relaxing.', 5, 16),
('Could use better heating.', 4, 16),

('Vintage and fun stay.', 4, 17),
('Location near beach was perfect.', 5, 17),

('Incredible villa and private pool.', 5, 18),
('Would love to come again!', 5, 18),

('Felt like royalty in this castle.', 5, 19),
('A bit cold inside, but amazing!', 4, 19),

('Desert view from the suite was unreal.', 5, 20),
('Perfect romantic getaway.', 5, 20),

('Cabin was peaceful and cozy.', 5, 21),
('Not much around, very isolated.', 3, 21),

('Breathtaking beach and sunset views.', 5, 22),
('Greek hospitality was amazing.', 5, 22),

('Loved the eco concept and nature setting.', 5, 23),
('Mosquitoes were an issue.', 3, 23),

('Quaint and peaceful cottage.', 4, 24),
('Loved the southern charm.', 5, 24),

('Compact but cozy Tokyo apartment.', 4, 25),
('Great location, but small bathroom.', 3, 25),

('Cabin was warm and well-equipped.', 5, 26),
('Loved spending evenings by the lake.', 5, 26),

('Perfect honeymoon villa.', 5, 27),
('Expensive but worth every rupee.', 5, 27),

('Great ski location, loved the decor.', 5, 28),
('Staff was very attentive.', 5, 28),

('Peaceful beach house with modern design.', 5, 29),
('Perfect family spot.', 4, 29),

('Loved everything about this loft.', 5, 30),
('Amazing view of the skyline.', 5, 30);
