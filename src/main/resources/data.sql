-- Testdata. Login: mia@test.dk / Test1234  (ejer)  og  gaest@test.dk / Test1234  (har reserveret et ønske)
-- Delt link til demo-listen: /share/demo0000000000000000000000000001
INSERT INTO users (user_name, user_email, user_password)
VALUES ('Mia', 'mia@test.dk', '$2a$10$Dbh/0m7b.KOB2.hukwsqE.Tp6dJg9LM7A09xjl7yaHxevAcnyKkWe'),
       ('Gæst', 'gaest@test.dk', '$2a$10$Dbh/0m7b.KOB2.hukwsqE.Tp6dJg9LM7A09xjl7yaHxevAcnyKkWe');

INSERT INTO wishlist (user_id, name, description, sharetoken)
VALUES (1, 'Mias 30-års fødselsdag', 'Ting jeg gerne vil have', 'demo0000000000000000000000000001');

INSERT INTO wish (wishlist_id, name, description, price, link)
VALUES (1, 'Keramikkande', 'Gerne i hvid', 349.00, 'https://example.com/kande'),
       (1, 'Løbesko', 'Størrelse 39', 899.00, NULL),
       (1, 'Koncertbillet', 'Oktober', 595.00, NULL);

INSERT INTO reservation (wish_id, user_id)
VALUES (2, 2);
