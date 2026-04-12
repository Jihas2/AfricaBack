-- ─── GALLERY IMAGES ──────────────────────────────────────────────────────────
CREATE TABLE gallery_images (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    url         VARCHAR(512)    NOT NULL,
    caption     VARCHAR(255),
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ─── AMENITY ITEMS ───────────────────────────────────────────────────────────
CREATE TABLE amenity_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    url         VARCHAR(512)    NOT NULL,
    icon        VARCHAR(50),
    name        VARCHAR(100)    NOT NULL,
    caption     VARCHAR(255),
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ─── Seed: galeria (fotos reais) ─────────────────────────────────────────────
INSERT INTO gallery_images (url, caption, sort_order) VALUES
('/images/gallery/IMG_1371.PNG', 'Une Adresse d''Exception au Coeur de Kinshasa', 1),
('/images/gallery/IMG_1372.PNG', 'Elegance & Modernite a Kinshasa / Gombe',       2),
('/images/gallery/IMG_1373.PNG', 'Confort & Prestige au Quotidien',               3),
('/images/gallery/IMG_1374.PNG', 'Vivre l''Excellence Urbaine',                   4);

-- ─── Seed: comodidades ───────────────────────────────────────────────────────
INSERT INTO amenity_items (url, icon, name, caption, sort_order) VALUES
('/images/renders/amenity-terrasse.jpg', null, 'Terrasse & Espace Exterieur', 'Detente et Vue Panoramique au Sommet',          1),
('/images/renders/amenity-sport.jpg',    null, 'Salle de Sport',              'Performance & Bien-Etre a Chaque Instant',      2),
('/images/renders/amenity-lobby.jpg',    null, 'Lobby',                       'Un Accueil a la Hauteur de Votre Standing',     3),
('/images/renders/amenity-design.jpg',   null, 'Design d''Interieur',         'Materiaux Nobles & Finitions Sur Mesure',       4),
('/images/renders/amenity-magasins.jpg', null, 'Magasins & Services',         'Tout ce dont Vous Avez Besoin, A Portee de Main',5),
('/images/renders/amenity-parking.jpg',  null, 'Parking 2500m2',              'Securite et Confort pour Tous les Residents',   6);
