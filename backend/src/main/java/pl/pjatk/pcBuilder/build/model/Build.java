package pl.pjatk.pcBuilder.build.model;

import jakarta.persistence.*;
import lombok.*;
import pl.pjatk.pcBuilder.build.model.components.*;
import pl.pjatk.pcBuilder.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Build")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cpu_id", nullable = false)
    private Cpu cpu;

    @ManyToOne
    @JoinColumn(name = "gpu_id", nullable = false)
    private VideoCard gpu;

    @ManyToOne
    @JoinColumn(name = "motherboard_id", nullable = false)
    private Motherboard motherboard;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "build_storage",
            joinColumns = @JoinColumn(name = "build_id"),
            inverseJoinColumns = @JoinColumn(name = "storage_id")
    )
    private List<HardDrive> storage = new ArrayList<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,})
    @JoinTable(
            name = "build_memory",
            joinColumns = @JoinColumn(name = "build_id"),
            inverseJoinColumns = @JoinColumn(name = "memory_id"))
    private List<Memory> memory = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "power_id", nullable = false)
    private PowerSupply powerSupply;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private PcCase pcCase;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
