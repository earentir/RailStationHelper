package pm.ear.railtrackhelper.data

data class Station(
    val nameEn: String,
    val nameEl: String,
    val lines: List<String>
)

data class MetroLine(
    val lineId: String,
    val stations: List<String> // station names in English
)

data class City(
    val name: String,
    val lines: List<MetroLine>,
    val stations: List<Station>
)

object MetroData {
    val athens = City(
        name = "Athens",
        lines = listOf(
            MetroLine(
                lineId = "M1",
                stations = listOf(
                    "Piraeus", "Faliro", "Moschato", "Kallithea", "Tavros-Eleftherios Venizelos",
                    "Petralona", "Thissio", "Monastiraki", "Omonia", "Victoria", "Attiki",
                    "Aghios Nikolaos", "Kato Patissia", "Aghios Eleftherios", "Ano Patissia",
                    "Perissos", "Pefkakia", "Nea Ionia", "Irakleio", "Eirini", "Neratziotissa",
                    "Maroussi", "KAT", "Kifissia"
                )
            ),
            MetroLine(
                lineId = "M2",
                stations = listOf(
                    "Anthoupoli", "Peristeri", "Aghios Antonios", "Sepolia", "Attiki",
                    "Larissa Station", "Metaxourghio", "Omonia", "Panepistimio", "Syntagma",
                    "Akropoli", "Syngrou-Fix", "Neos Kosmos", "Aghios Ioannis", "Dafni",
                    "Aghios Dimitrios/Alexandros Panagoulis", "Ilioupoli/Grigoris Lambrakis",
                    "Alimos", "Argyroupoli", "Elliniko"
                )
            ),
            MetroLine(
                lineId = "M3",
                stations = listOf(
                    "Dimotiko Theatro", "Piraeus", "Maniatika", "Nikaia", "Korydallos",
                    "Agia Varvara", "Agia Marina", "Egaleo", "Eleonas", "Kerameikos",
                    "Monastiraki", "Syntagma", "Evangelismos", "Megaro Moussikis",
                    "Ambelokipi", "Panormou", "Katehaki", "Ethniki Amyna", "Holargos",
                    "Nomismatokopio", "Agia Paraskevi", "Halandri", "Doukissis Plakentias",
                    "Pallini", "Paiania-Kantza", "Koropi", "Airport"
                )
            )
        ),
        stations = listOf(
            Station("Piraeus", "Πειραιάς", listOf("M1", "M3")),
            Station("Faliro", "Φάληρο", listOf("M1")),
            Station("Moschato", "Μοσχάτο", listOf("M1")),
            Station("Kallithea", "Καλλιθέα", listOf("M1")),
            Station("Tavros-Eleftherios Venizelos", "Ταύρος-Ελευθέριος Βενιζέλος", listOf("M1")),
            Station("Petralona", "Πετράλωνα", listOf("M1")),
            Station("Thissio", "Θησείο", listOf("M1")),
            Station("Monastiraki", "Μοναστηράκι", listOf("M1", "M3")),
            Station("Omonia", "Ομόνοια", listOf("M1", "M2")),
            Station("Victoria", "Βικτώρια", listOf("M1")),
            Station("Attiki", "Αττική", listOf("M1", "M2")),
            Station("Aghios Nikolaos", "Άγιος Νικόλαος", listOf("M1")),
            Station("Kato Patissia", "Κάτω Πατήσια", listOf("M1")),
            Station("Aghios Eleftherios", "Άγιος Ελευθέριος", listOf("M1")),
            Station("Ano Patissia", "Άνω Πατήσια", listOf("M1")),
            Station("Perissos", "Περισσός", listOf("M1")),
            Station("Pefkakia", "Πευκάκια", listOf("M1")),
            Station("Nea Ionia", "Νέα Ιωνία", listOf("M1")),
            Station("Irakleio", "Ηράκλειο", listOf("M1")),
            Station("Eirini", "Ειρήνη", listOf("M1")),
            Station("Neratziotissa", "Νερατζιώτισσα", listOf("M1")),
            Station("Maroussi", "Μαρούσι", listOf("M1")),
            Station("KAT", "ΚΑΤ", listOf("M1")),
            Station("Kifissia", "Κηφισιά", listOf("M1")),
            Station("Anthoupoli", "Ανθούπολη", listOf("M2")),
            Station("Peristeri", "Περιστέρι", listOf("M2")),
            Station("Aghios Antonios", "Άγιος Αντώνιος", listOf("M2")),
            Station("Sepolia", "Σεπόλια", listOf("M2")),
            Station("Larissa Station", "Σταθμός Λαρίσης", listOf("M2")),
            Station("Metaxourghio", "Μεταξουργείο", listOf("M2")),
            Station("Panepistimio", "Πανεπιστήμιο", listOf("M2")),
            Station("Syntagma", "Σύνταγμα", listOf("M2", "M3")),
            Station("Akropoli", "Ακρόπολη", listOf("M2")),
            Station("Syngrou-Fix", "Συγγρού-Φιξ", listOf("M2")),
            Station("Neos Kosmos", "Νέος Κόσμος", listOf("M2")),
            Station("Aghios Ioannis", "Άγιος Ιωάννης", listOf("M2")),
            Station("Dafni", "Δάφνη", listOf("M2")),
            Station("Aghios Dimitrios/Alexandros Panagoulis", "Άγιος Δημήτριος/Αλέξανδρος Παναγούλης", listOf("M2")),
            Station("Ilioupoli/Grigoris Lambrakis", "Ηλιούπολη/Γρηγόρης Λαμπράκης", listOf("M2")),
            Station("Alimos", "Άλιμος", listOf("M2")),
            Station("Argyroupoli", "Αργυρούπολη", listOf("M2")),
            Station("Elliniko", "Ελληνικό", listOf("M2")),
            Station("Dimotiko Theatro", "Δημοτικό Θέατρο", listOf("M3")),
            Station("Maniatika", "Μανιάτικα", listOf("M3")),
            Station("Nikaia", "Νίκαια", listOf("M3")),
            Station("Korydallos", "Κορυδαλλός", listOf("M3")),
            Station("Agia Varvara", "Αγία Βαρβάρα", listOf("M3")),
            Station("Agia Marina", "Αγία Μαρίνα", listOf("M3")),
            Station("Egaleo", "Αιγάλεω", listOf("M3")),
            Station("Eleonas", "Ελαιώνας", listOf("M3")),
            Station("Kerameikos", "Κεραμεικός", listOf("M3")),
            Station("Evangelismos", "Ευαγγελισμός", listOf("M3")),
            Station("Megaro Moussikis", "Μέγαρο Μουσικής", listOf("M3")),
            Station("Ambelokipi", "Αμπελόκηποι", listOf("M3")),
            Station("Panormou", "Πανόρμου", listOf("M3")),
            Station("Katehaki", "Κατεχάκη", listOf("M3")),
            Station("Ethniki Amyna", "Εθνική Άμυνα", listOf("M3")),
            Station("Holargos", "Χολαργός", listOf("M3")),
            Station("Nomismatokopio", "Νομισματοκοπείο", listOf("M3")),
            Station("Agia Paraskevi", "Αγία Παρασκευή", listOf("M3")),
            Station("Halandri", "Χαλάνδρι", listOf("M3")),
            Station("Doukissis Plakentias", "Δουκίσσης Πλακεντίας", listOf("M3")),
            Station("Pallini", "Παλλήνη", listOf("M3")),
            Station("Paiania-Kantza", "Παιανία-Κάντζα", listOf("M3")),
            Station("Koropi", "Κορωπί", listOf("M3")),
            Station("Airport", "Αεροδρόμιο", listOf("M3"))
        ).sortedBy { it.nameEn }
    )
    val thessaloniki = City(
        name = "Thessaloniki",
        lines = listOf(
            MetroLine(
                lineId = "T1",
                stations = listOf(
                    "Neos Sidirodromikos Stathmos", "Dimokratias", "Venizelou", "Agia Sofia",
                    "Sintrivani/Ekthesi", "Panepistimio", "Papafi", "Efklidi", "Fleming",
                    "Analipseos", "25 Martiou", "Voulgari", "Nea Elvetia"
                )
            ),
            MetroLine(
                lineId = "T2",
                stations = listOf(
                    "25 Martiou", "Nomarchia", "Kalamaria", "Aretsou", "Nea Krini", "Mikra"
                )
            )
        ),
        stations = listOf(
            Station("Neos Sidirodromikos Stathmos", "Νέος Σιδηροδρομικός Σταθμός", listOf("T1")),
            Station("Dimokratias", "Δημοκρατίας", listOf("T1")),
            Station("Venizelou", "Βενιζέλου", listOf("T1")),
            Station("Agia Sofia", "Αγίας Σοφίας", listOf("T1")),
            Station("Sintrivani/Ekthesi", "Σιντριβάνι/Έκθεση", listOf("T1")),
            Station("Panepistimio", "Πανεπιστήμιο", listOf("T1")),
            Station("Papafi", "Παπάφη", listOf("T1")),
            Station("Efklidi", "Ευκλείδη", listOf("T1")),
            Station("Fleming", "Φλέμινγκ", listOf("T1")),
            Station("Analipseos", "Αναλήψεως", listOf("T1")),
            Station("25 Martiou", "25ης Μαρτίου", listOf("T1", "T2")),
            Station("Voulgari", "Βούλγαρη", listOf("T1")),
            Station("Nea Elvetia", "Νέα Ελβετία", listOf("T1")),
            Station("Nomarchia", "Νομαρχία", listOf("T2")),
            Station("Kalamaria", "Καλαμαριά", listOf("T2")),
            Station("Aretsou", "Αρετσού", listOf("T2")),
            Station("Nea Krini", "Νέα Κρήνη", listOf("T2")),
            Station("Mikra", "Μίκρα", listOf("T2"))
        ).sortedBy { it.nameEn }
    )

    val cities = listOf(athens, thessaloniki)
}
