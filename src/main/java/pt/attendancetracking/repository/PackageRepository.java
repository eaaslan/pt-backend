// PackageRepository.java
package pt.attendancetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.attendancetracking.model.Package;

public interface PackageRepository extends JpaRepository<Package, Long> {
}
