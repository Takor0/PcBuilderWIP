package pl.pjatk.pcBuilder.build.service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.components.*;
import pl.pjatk.pcBuilder.build.repository.*;

@Service
@RequiredArgsConstructor
public class DataImportService {
    private static final Logger logger = LoggerFactory.getLogger(DataImportService.class);
    private final CpuRepository cpuRepository;
    private final VideoCardRepository videoCardRepository;
    private final MotherboardRepository motherboardRepository;
    private final HardDriveRepository hardDriveRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final PcCaseRepository pcCaseRepository;
    private final MemoryRepository memoryRepository;

    private static final String DATA_DIR = "data-staging/csv/";

    @PostConstruct
    public void importData() {
        logger.info("Rozpoczynam import danych");
        clearAllTables();
        importCpus();
        importGpus();
        importMotherboards();
        importStorage();
        importPowerSupplies();
        importCases();
        importMemory();
        logger.info("Zakończono import danych");
    }

    private void clearAllTables() {
        logger.info("Czyszczenie tabel przed importem");
        pcCaseRepository.deleteAll();
        powerSupplyRepository.deleteAll();
        hardDriveRepository.deleteAll();
        motherboardRepository.deleteAll();
        videoCardRepository.deleteAll();
        cpuRepository.deleteAll();
        memoryRepository.deleteAll();
    }

    private void importCpus() {
        logger.info("Importowanie procesorów");
        Path csvPath = Paths.get(DATA_DIR, "cpu.csv");
        List<Cpu> cpus = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            
            while ((line = reader.readNext()) != null) {
                Cpu cpu = new Cpu();
                cpu.setName(line[0]);
                cpu.setPrice(parseDouble(line[1]));
                cpu.setCoreCount(parseInt(line[2]));
                cpu.setCoreClock(parseDouble(line[3]));
                cpu.setBoostClock(parseDouble(line[4]));
                cpu.setTdp(parseInt(line[6]));
                cpu.setHasGraphics(!line[7].isEmpty());
                cpu.setBrand(line[0].toUpperCase().contains("AMD") ? "AMD" : "INTEL");
                cpu.setSocket(determineSocket(line[0]));
                
                cpus.add(cpu);
            }
            
            cpuRepository.saveAll(cpus);
            logger.info("Zaimportowano {} procesorów", cpus.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania procesorów: {}", e.getMessage());
        }
    }

    private void importGpus() {
        logger.info("Importowanie kart graficznych");
        Path csvPath = Paths.get(DATA_DIR, "video-card.csv");
        List<VideoCard> gpus = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            int lineNumber = 0;

            while ((line = reader.readNext()) != null) {
                lineNumber++;

                try {
                    if (line.length < 6) {
                        logger.warn("Pominięto linię {}: zbyt mało danych", lineNumber);
                        continue;
                    }

                    if (line[0].isEmpty() || line[1].isEmpty()) {
                        logger.warn("Pominięto linię {}: brak nazwy lub ceny", lineNumber);
                        continue;
                    }

                    VideoCard gpu = new VideoCard();
                    gpu.setName(line[0].trim());
                    gpu.setPrice(parseDouble(line[1]));
                    gpu.setChipset(line[2].trim());
                    gpu.setMemory(parseInt(line[3]));
                    gpu.setCoreClock(parseDouble(line[4]));
                    gpu.setBoostClock(parseDouble(line[5]));
                    gpu.setTdp(150); // Default TDP

                    String chipset = gpu.getChipset().toLowerCase();
                    if (chipset.contains("radeon")) {
                        gpu.setBrand("AMD");
                    } else if (chipset.contains("geforce") || chipset.contains("rtx")) {
                        gpu.setBrand("NVIDIA");
                    } else {
                        gpu.setBrand("UNKNOWN");
                        logger.warn("Nie rozpoznano marki dla chipsetu: {} w linii {}", chipset, lineNumber);
                    }

                    if (gpu.getPrice() > 0 && gpu.getMemory() > 0) {
                        gpus.add(gpu);
                    }
                } catch (Exception e) {
                    logger.error("Błąd podczas przetwarzania linii {}: {}", lineNumber, e.getMessage());
                }
            }

            if (!gpus.isEmpty()) {
                videoCardRepository.saveAll(gpus);
                logger.info("Zaimportowano {} kart graficznych", gpus.size());
            } else {
                logger.info("Nie zaimportowano żadnych kart graficznych");
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania kart graficznych: {}", e.getMessage());
        }
    }


    private void importMotherboards() {
        logger.info("Importowanie płyt głównych");
        Path csvPath = Paths.get(DATA_DIR, "motherboard.csv");
        List<Motherboard> motherboards = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            
            while ((line = reader.readNext()) != null) {
                Motherboard mb = new Motherboard();
                mb.setName(line[0]);
                if (line[1].isEmpty()) continue; // Skip if no price
                mb.setPrice(parseDouble(line[1]));
                mb.setSocket(line[2]);
                mb.setFormFactor(line[3]);
                mb.setMaxMemory(128); // Default max memory
                mb.setMemorySlots(4); // Default memory slots
                mb.setMaxMemorySpeed(parseDouble(line[4]));
                mb.setHasWifi(line[5].toLowerCase().contains("wifi"));
                
                motherboards.add(mb);
            }
            
            motherboardRepository.saveAll(motherboards);
            logger.info("Zaimportowano {} płyt głównych", motherboards.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania płyt głównych: {}", e.getMessage());
        }
    }

    private void importStorage() {
        logger.info("Importowanie dysków");
        Path csvPath = Paths.get(DATA_DIR, "internal-hard-drive.csv");
        List<HardDrive> drives = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            
            while ((line = reader.readNext()) != null) {
                HardDrive drive = new HardDrive();
                drive.setName(line[0]);
                if (line[1].isEmpty()) continue; // Skip if no price
                drive.setPrice(parseDouble(line[1]));
                drive.setCapacity(parseInt(line[2]));
                drive.setType(determineStorageType(line[3]));
                drive.setFormFactor(line[4]);
                drive.setInterfaceType(line[5]);
                
                drives.add(drive);
            }
            
            hardDriveRepository.saveAll(drives);
            logger.info("Zaimportowano {} dysków", drives.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania dysków: {}", e.getMessage());
        }
    }

    private void importPowerSupplies() {
        logger.info("Importowanie zasilaczy");
        Path csvPath = Paths.get(DATA_DIR, "power-supply.csv");
        logger.debug("Ścieżka do pliku CSV: {}", csvPath.toAbsolutePath());
        List<PowerSupply> psus = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            int lineNumber = 0;
            
            while ((line = reader.readNext()) != null) {
                lineNumber++;
                try {
                    if (line.length < 6) {
                        logger.warn("Linia {} ma za mało kolumn, pomijam", lineNumber);
                        continue;
                    }

                    PowerSupply psu = new PowerSupply();
                    psu.setName(line[0].trim());
                    psu.setPrice(parseDouble(line[1]));
                    psu.setType(line[2].trim());
                    psu.setEfficiency(line[3].trim());
                    psu.setWattage(parseInt(line[4]));
                    psu.setModular(line[5].toLowerCase().contains("full"));
                    
                    if (psu.getPrice() > 0 && psu.getWattage() > 0) {
                        psus.add(psu);
                        logger.debug("Dodano zasilacz: {} ({}W, {} PLN)", 
                            psu.getName(), psu.getWattage(), psu.getPrice());
                    } else {
                        logger.warn("Pominięto zasilacz z nieprawidłową ceną lub mocą: {} (cena: {}, moc: {}W)", 
                            psu.getName(), psu.getPrice(), psu.getWattage());
                    }
                } catch (Exception e) {
                    logger.error("Błąd podczas parsowania linii {} zasilacza: {}", lineNumber, e.getMessage());
                }
            }
            
            if (!psus.isEmpty()) {
                powerSupplyRepository.saveAll(psus);
                logger.info("Zaimportowano {} zasilaczy", psus.size());
            } else {
                logger.warn("Nie znaleziono żadnych poprawnych zasilaczy do zaimportowania");
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania zasilaczy: {}", e.getMessage());
        }
    }

    private void importCases() {
        logger.info("Importowanie obudów");
        Path csvPath = Paths.get(DATA_DIR, "case.csv");
        List<PcCase> cases = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            
            while ((line = reader.readNext()) != null) {
                PcCase pcCase = new PcCase();
                pcCase.setName(line[0]);
                if (line[1].isEmpty()) continue; // Skip if no price
                pcCase.setPrice(parseDouble(line[1]));
                pcCase.setType(line[2]);
                pcCase.setFormFactor(line[3]);
                pcCase.setHasRgb(line[0].toLowerCase().contains("rgb"));
                
                cases.add(pcCase);
            }
            
            pcCaseRepository.saveAll(cases);
            logger.info("Zaimportowano {} obudów", cases.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania obudów: {}", e.getMessage());
        }
    }


    private void importMemory() {
        logger.info("Importowanie pamięci RAM");
        Path csvPath = Paths.get(DATA_DIR, "memory.csv");
        List<Memory> memories = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext();
            String[] line;

            while ((line = reader.readNext()) != null) {
                Memory memory = new Memory();
                memory.setName(line[0]);
                if (line[1].isEmpty()) continue;
                memory.setPrice(parseDouble(line[1]));
                memory.setSpeed(parseDouble(line[2].replace(",", ".")));
                memory.setModules(parseInt(line[3].split(",")[0]));
                memory.setCapacity(parseInt(line[3].split(",")[1]));
                memory.setPricePerGb(parseDouble(line[4]));
                memory.setFirstWordLatency(parseDouble(line[6]));
                memory.setCasLatency(parseInt(line[7]));

                memories.add(memory);
            }

            memoryRepository.saveAll(memories);
            logger.info("Zaimportowano {} pamięci RAM", memories.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Błąd podczas importowania pamięci RAM: {}", e.getMessage());
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            String cleaned = value.replaceAll("[^\\d.,]", "").replace(",", ".");
            if (cleaned.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            String cleaned = value.replaceAll("[^\\d]", "");
            if (cleaned.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String determineStorageType(String name) {
        name = name.toLowerCase();
        if (name.contains("nvme") || name.contains("pcie")) {
            return "NVMe";
        } else if (name.contains("ssd")) {
            return "SSD";
        } else {
            return "HDD";
        }
    }

    private String determineSocket(String name) {
        name = name.toLowerCase();
        if (name.contains("amd") || name.contains("ryzen")) {
            if (name.contains("7")) {
                return "AM5";
            } else {
                return "AM4";
            }
        } else {
            if (name.contains("12") || name.contains("13")) {
                return "LGA1700";
            } else {
                return "LGA1200";
            }
        }
    }
} 