package com.apettigrew.invoice;

import com.apettigrew.invoice.dtos.ContactInfoDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties(value = {ContactInfoDto.class})
@ComponentScan(basePackageClasses = InvoiceApplication.class)
public class InvoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceApplication.class, args);
	}

	/**
	 * Provides a primary ModelMapper bean to handle conversion of DTOs to and from Resources.
	 *
	 * @return a configured ModelMapper instance
	 */
	@Primary
	public ModelMapper modelMapper() {
		final var mapper = new ModelMapper();
		mapper.getConfiguration()
				.setMatchingStrategy(MatchingStrategies.STRICT)
				.setFieldMatchingEnabled(true);
		return mapper;
	}
}
