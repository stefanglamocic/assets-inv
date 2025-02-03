
# Registar osnovnih sredstava

Projektni zadatak iz Mobilnog računarstva 2024/2025


## Preduslovi

Kako bi funkcionisale google mape, u korijeni direktorijum je potrebno dodati fajl `secrets.properties` sa sljedećim sadržajem:
`MAPS_API_KEY=<KLJUČ>`

Zamijenite `<KLJUČ>` sa vašim stvarnim api ključem koji možete generisati na [Google-ovoj Credentials stranici](https://console.cloud.google.com/apis/credentials)


## Projektni zahtjevi

Napisati Android aplikaciju koja predstavlja registar osnovnih sredstava (OS) neke
firme. OS može biti bilo kakav resurs (sto, stolica, računar, lampa, čaša itd.), pri čemu svako
OS ima minimalno sljedeće atribute: naziv, opis, barkod (cijeli broj), cijena, datum kreiranja,
zadužena osoba (zaposleni), zadužena lokacija (grad u kojem se vodi OS) i slika.


U okviru aplikacije, potrebno je u vidu liste omogućiti prikaz minimalno sljedećih
kategorija: osnovna sredstva, zaposleni, lokacije i popisne liste. Omogućiti pretragu stavki za
svaku od lista, po barem dva kriterijuma.


Potrebno je omogućiti CRUD operacije za svaku od kategorija, pri čemu se podaci
čuvaju u lokalnoj (SQLite) bazi. Prilikom dodavanja OS-a, omogućiti unos vrijednosti barkoda
ručno ili skeniranjem barkoda (pomoću kamere). Skeniranjem barkoda, njegova cjelobrojna
vrijednost se upisuje u odgovarajuće polje na formi za unos OS-a. Sliku je moguće upload-
ovati sa uređaja ili snimiti pomoću kamere uređaja.


Potrebno je omogućiti kreiranje popisnih lista. Jedna popisna lista sadrži proizvoljan
broj stavki, pri čemu svaka stavka sadrži: OS, osobu na kojoj se trenutno vodi OS, osobu na
koju se prenosi OS, trenutnu lokaciju OS-a, novu lokaciju OS-a. Ukoliko OS ostaje na istoj
osobi, onda je trenutna osoba = nova osoba. Ukoliko OS ne mijenja lokaciju, onda je
trenutna lokacija = nova lokacija (oba polja imaju istu vrijednost). Prilikom kreiranja nove
stavke, omogućiti skeniranje barkoda OS-a, pri čemu se automatski popunjavaju ostali
podaci vezani za skenirano OS (naziv, zadužena osoba, zadužena lokacija itd.). Za
funkcionalnost skeniranja barkoda, dozvoljeno je korištenje eksternih biblioteka (npr. ZXing).

Za svako OS omogućiti prikaz detalja, pri čemu se prikazuju osnovne informacije,
slika, trenutno zadužena osoba i lokacija. Lokaciju je moguće vidjeti i na mapi, pri čemu je
potrebno prikazati odgovarajući pin koji pokazuje na zadati grad. Klikom na pin, potrebno je
prikazati listu svih osnovnih sredstava koja se trenutno vode u tom gradu.

U okviru posebne stranice sa podešavanjima, omogućiti izbor jezika, pri čemu je
potrebno omogućiti da aplikacija radi minimalno na srpskom i engleskom jeziku.

Sve aktivnosti koje bi mogle uticati na blokiranje glavne niti aplikacije, potrebno je
realizovati asinhrono. Za ovu namjenu potrebno je koristiti klasu AsyncTask, ili neku od
biblioteka koje pružaju ovu funkcionalnost.

Potrebno je generisati grafičke elemente tako da se pokrije nekoliko različitih
aktuelnih gustina ekrana kao i različitih dimenzija ekrana. Za generisanje grafičkih resursa,
dozvoljeno je korištenje nekog od alata kao što je AndroidAssetStudio. Prilikom izgradnje
grafičkih dijelova aplikacije (Layout Manager-i, View-ovi), potrebno je voditi računa o
performansama aplikacije. Koristiti vektorske grafičke elemente gdje god je to moguće.
