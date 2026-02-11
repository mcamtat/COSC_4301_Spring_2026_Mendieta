INSERT INTO habitats (id, biome, location, min_temp_c, max_temp_c, created_at)
VALUES
    (1, 'FOREST', 'The Silent Forest', 8, 18, NOW()),
    (2, 'FOREST', 'Greenglade', 10, 22, NOW()),
    (3, 'DESERT', 'The Burning Lands', 35, 55, NOW()),
    (4, 'DESERT', 'The Sun Disc', 30, 48, NOW()),
    (5, 'OCEAN', 'Shadow Isles', 2, 6, NOW()),
    (6, 'OCEAN', 'Bilgewater Bay', 18, 26, NOW()),
    (7, 'SWAMP', 'The Serpentine Delta', 20, 32, NOW()),
    (8, 'ARCTIC', 'Ursine Lands', -30, -10, NOW()),
    (9, 'JUNGLE', 'Ixtal Jungle', 22, 38, NOW()),
    (10, 'MOUNTAIN', 'Bloodcliffs', 15, 28, NOW());

INSERT INTO creatures (name, species, danger_level, condition, notes, habitat_id, created_at)
VALUES
    ('Fae Fawn', 'Spirit', 'LOW', 'QUARANTINED', 'Skittish forest spirit; sensitive to iron.', 1, NOW()),
    ('Yordle', 'Spirit', 'LOW', 'STABLE', 'Small magical being; highly curious.', 1, NOW()),
    ('Treant', 'Spirit', 'LOW', 'STABLE', 'Ancient tree guardian; slow but resilient.', 2, NOW()),
    ('Brambleback', 'Spirit', 'MEDIUM', 'QUARANTINED', 'Aggressive jungle brute with burning hide.', 4, NOW()),
    ('River Sprite', 'Spirit', 'LOW', 'CRITICAL', 'Water-bound spirit weakened outside its stream.', 5, NOW()),

    ('Celestial Dragon', 'Dragon', 'HIGH', 'CRITICAL', 'Radiates cosmic energy; unstable power surges.', 7, NOW()),
    ('Terrestrial Dragon', 'Dragon', 'HIGH', 'QUARANTINED', 'Earth-shaking presence; extremely territorial.', 7, NOW()),
    ('Basilisk', 'Dragon', 'HIGH', 'STABLE', 'Petrifying gaze; avoid direct eye contact.', 3, NOW()),
    ('Wyvern', 'Dragon', 'HIGH', 'STABLE', 'Fast aerial predator with venomous tail.', 10, NOW()),

    ('Revenant', 'Undead', 'HIGH', 'STABLE', 'Reanimated warrior bound by dark magic.', 4, NOW()),
    ('Wraith', 'Undead', 'MEDIUM', 'CRITICAL', 'Intangible spirit feeding on fear.', 5, NOW()),

    ('Minion', 'Golem', 'LOW', 'STABLE', 'Constructed servant animated by arcane energy.', 6, NOW()),
    ('Krug', 'Golem', 'LOW', 'STABLE', 'Rock-like creature that splits when damaged.', 9, NOW()),

    ('Aspect Host', 'Ascended', 'HIGH', 'STABLE', 'Mortal vessel empowered by celestial force.', 4, NOW()),
    ('Baccai', 'Ascended', 'HIGH', 'QUARANTINED', 'Failed ascension; unstable transformation.', 4, NOW()),
    ('Darkin', 'Ascended', 'HIGH', 'STABLE', 'Ancient corrupted warrior bound to weapon.', 4, NOW()),
    ('God-Warrior', 'Ascended', 'HIGH', 'CRITICAL', 'Divine soldier forged through ritual ascension.', 4, NOW()),

    ('Murk Wolf', 'Wolf', 'LOW', 'STABLE', 'Pack predator thriving in dense fog.', 8, NOW()),

    ('Meep', 'Celestial', 'LOW', 'STABLE', 'Small cosmic entity drawn to magical energy.', 8, NOW()),
    ('Sparklefly', 'Celestial', 'LOW', 'QUARANTINED', 'Glowing insect emitting disruptive stardust.', 8, NOW()),

    ('Xer''Sai', 'Voidborn', 'MEDIUM', 'CRITICAL', 'Burrowing void predator sensing surface vibrations.', 9, NOW()),
    ('Voidgrub', 'Voidborn', 'LOW', 'STABLE', 'Larval void creature; rapidly reproduces.', 9, NOW()),

    ('Sea Serpent', 'Aquian', 'LOW', 'STABLE', 'Massive ocean predator dwelling in deep currents.', 5, NOW()),
    ('Waverider', 'Aquian', 'MEDIUM', 'STABLE', 'Amphibious hunter navigating coastal zones.', 6, NOW());
