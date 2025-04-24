package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
