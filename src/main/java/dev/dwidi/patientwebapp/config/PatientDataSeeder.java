package dev.dwidi.patientwebapp.config;

import dev.dwidi.patientwebapp.entity.Patient;
import dev.dwidi.patientwebapp.entity.embedded.AustralianAddress;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.enums.Gender;
import dev.dwidi.patientwebapp.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientDataSeeder implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final Random random = new Random();
    private final AtomicInteger sequenceNumber = new AtomicInteger(1);

    private final String[] FIRST_NAMES = {
            "James", "John", "Robert", "Michael", "William", "David", "Joseph", "Thomas", "Charles", "Christopher",
            "Emma", "Olivia", "Ava", "Isabella", "Sophia", "Mia", "Charlotte", "Amelia", "Harper", "Evelyn",
            "Daniel", "Matthew", "Anthony", "Donald", "Mark", "Paul", "Steven", "Andrew", "Kenneth", "Joshua",
            "Elizabeth", "Margaret", "Catherine", "Sarah", "Patricia", "Jennifer", "Linda", "Barbara", "Susan", "Jessica"
    };

    private final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Wilson", "Anderson", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Thompson", "White", "Harris",
            "Clark", "Lewis", "Robinson", "Walker", "Young", "Hall", "Allen", "King", "Wright", "Scott",
            "Green", "Baker", "Adams", "Nelson", "Hill", "Campbell", "Mitchell", "Roberts", "Carter", "Phillips"
    };

    private final String[] SUBURBS = {
            "Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide", "Gold Coast", "Newcastle", "Canberra",
            "Wollongong", "Logan", "Hobart", "Townsville", "Cairns", "Darwin", "Geelong", "Ballarat",
            "Bendigo", "Albury", "Wodonga", "Launceston", "Mackay", "Rockhampton", "Bunbury", "Mandurah",
            "Bundaberg", "Wagga Wagga", "Hervey Bay", "Mildura", "Gladstone", "Shepparton"
    };

    private final String[] STREET_NAMES = {
            "Wattle", "Eucalyptus", "Banksia", "Acacia", "Waratah", "Bottlebrush", "Jarrah", "Boronia",
            "Grevillea", "Melaleuca", "Kangaroo", "Koala", "Wallaby", "Platypus", "Dingo", "Kookaburra",
            "Rosemary", "Jasmine", "Orchid", "Rose", "Lily", "Daisy", "Violet", "Iris", "Magnolia", "Palm",
            "Cedar", "Pine", "Birch", "Oak"
    };

    private final String[] STREET_TYPES = {
            "Street", "Avenue", "Road", "Drive", "Court", "Place", "Way", "Close", "Lane", "Circuit",
            "Parade", "Boulevard", "Crescent", "Grove", "Terrace"
    };

    @Override
    public void run(String... args) {
        if (isDatabaseEmpty()) {
            generateDummyData();
        }
    }

    private boolean isDatabaseEmpty() {
        return patientRepository.count() == 0;
    }

    private void generateDummyData() {
        log.info("Starting to generate dummy patient data...");
        List<Patient> patients = new ArrayList<>();

        sequenceNumber.set(1);

        for (int i = 0; i < 1000; i++) {
            Patient patient = createDummyPatient();
            patient.setPid(generateUniquePid());
            patients.add(patient);

            if (i > 0 && i % 100 == 0) {
                patientRepository.saveAll(patients);
                patients.clear();
                log.info("Saved batch of 100 patients, progress: {}/1000", i);
            }
        }

        if (!patients.isEmpty()) {
            patientRepository.saveAll(patients);
        }
        log.info("Successfully generated 1000 dummy patient records");
    }

    private String generateUniquePid() {
        LocalDate now = LocalDate.now();

        int sequence = sequenceNumber.getAndIncrement();

        return String.format("%03d%02d%02d%02d",
                sequence,
                now.getDayOfMonth(),
                now.getMonthValue(),
                now.getYear() % 100);
    }

    private Patient createDummyPatient() {
        Patient patient = new Patient();
        patient.setFirstName(getRandomElement(FIRST_NAMES));
        patient.setLastName(getRandomElement(LAST_NAMES));
        patient.setDateOfBirth(generateRandomDateOfBirth());
        patient.setGender(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
        patient.setPhoneNumber(generateRandomPhoneNumber());
        patient.setAddress(generateRandomAddress());

        // Set a random createdAt date within your test range
        LocalDateTime randomCreatedAt = LocalDateTime.of(2024, 1,
                random.nextInt(31) + 1,  // day between 1-31
                random.nextInt(23),      // hour
                random.nextInt(59),      // minute
                random.nextInt(59)       // second
        );
        patient.setCreatedAt(randomCreatedAt);

        return patient;
    }

    private LocalDate generateRandomDateOfBirth() {
        int minAge = 18;
        int maxAge = 90;
        int randomAge = random.nextInt(maxAge - minAge + 1) + minAge;

        return LocalDate.now().minusYears(randomAge)
                .minusDays(random.nextInt(365));
    }

    private String generateRandomPhoneNumber() {
        return String.format("04%08d", random.nextInt(100000000));
    }

    private AustralianAddress generateRandomAddress() {
        AustralianAddress address = new AustralianAddress();

        address.setAddress(String.format("%d %s %s",
                random.nextInt(150) + 1,
                getRandomElement(STREET_NAMES),
                getRandomElement(STREET_TYPES)));

        address.setSuburb(getRandomElement(SUBURBS));
        address.setState(getRandomElement(AustralianState.values()));
        address.setPostcode(generateRandomPostcode(address.getState()));

        return address;
    }

    private String generateRandomPostcode(AustralianState state) {
        return switch (state) {
            case NSW -> String.format("%04d", 2000 + random.nextInt(999));  // 2000-2999
            case ACT -> String.format("%04d", 2600 + random.nextInt(18));   // 2600-2618
            case VIC -> String.format("%04d", 3000 + random.nextInt(999));  // 3000-3999
            case QLD -> String.format("%04d", 4000 + random.nextInt(999));  // 4000-4999
            case SA -> String.format("%04d", 5000 + random.nextInt(799));   // 5000-5799
            case WA -> String.format("%04d", 6000 + random.nextInt(797));   // 6000-6797
            case TAS -> String.format("%04d", 7000 + random.nextInt(999));  // 7000-7999
            case NT -> String.format("%04d", 800 + random.nextInt(99));     // 0800-0899
        };
    }

    private <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
}