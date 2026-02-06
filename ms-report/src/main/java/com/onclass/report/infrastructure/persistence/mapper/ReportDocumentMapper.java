package com.onclass.report.infrastructure.persistence.mapper;

import com.onclass.report.domain.model.BootcampReport;
import com.onclass.report.domain.model.CapacitySnapshot;
import com.onclass.report.domain.model.PersonSnapshot;
import com.onclass.report.domain.model.TechnologySnapshot;
import com.onclass.report.infrastructure.persistence.entity.BootcampReportDocument;
import com.onclass.report.infrastructure.persistence.entity.pojo.CapacityDoc;
import com.onclass.report.infrastructure.persistence.entity.pojo.PersonDoc;
import com.onclass.report.infrastructure.persistence.entity.pojo.TechnologyDoc;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDocumentMapper {
    public BootcampReportDocument toDocument(BootcampReport d) {
        BootcampReportDocument doc = new BootcampReportDocument();
        doc.setId(d.getId());
        doc.setBootcampId(d.getBootcampId());
        doc.setName(d.getName());
        doc.setDescription(d.getDescription());
        doc.setStartDate(d.getStartDate());
        doc.setWeeksDuration(d.getWeeksDuration());
        doc.setCapacities(d.getCapacities() != null
                ? d.getCapacities().stream()
                .map(c -> new CapacityDoc(c.id(), c.name(), c.description(),
                        c.technologies() != null
                                ? c.technologies().stream()
                                .map(t -> new TechnologyDoc(t.id(), t.name())).toList()
                                : List.of(),
                        c.technologyCount()))
                .toList()
                : List.of());
        doc.setCapacityCount(d.getCapacityCount());
        doc.setTechnologyCount(d.getTechnologyCount());
        doc.setPersons(d.getPersons() != null
                ? d.getPersons().stream()
                .map(p -> new PersonDoc(p.id(), p.name(), p.email())).toList()
                : List.of());
        doc.setPersonCount(d.getPersonCount());
        doc.setReportedAt(d.getReportedAt());
        doc.setUpdatedAt(d.getUpdatedAt());
        return doc;
    }

    public BootcampReport toDomain(BootcampReportDocument doc) {
        BootcampReport r = new BootcampReport();
        r.setId(doc.getId());
        r.setBootcampId(doc.getBootcampId());
        r.setName(doc.getName());
        r.setDescription(doc.getDescription());
        r.setStartDate(doc.getStartDate());
        r.setWeeksDuration(doc.getWeeksDuration());
        r.setCapacities(doc.getCapacities() != null
                ? doc.getCapacities().stream()
                .map(c -> new CapacitySnapshot(c.id(), c.name(), c.description(),
                        c.technologies() != null
                                ? c.technologies().stream()
                                .map(t -> new TechnologySnapshot(t.id(), t.name())).toList()
                                : List.of(),
                        c.technologyCount()))
                .toList()
                : List.of());
        r.setCapacityCount(doc.getCapacityCount());
        r.setTechnologyCount(doc.getTechnologyCount());
        r.setPersons(doc.getPersons() != null
                ? doc.getPersons().stream()
                .map(p -> new PersonSnapshot(p.id(), p.name(), p.email())).toList()
                : List.of());
        r.setPersonCount(doc.getPersonCount());
        r.setReportedAt(doc.getReportedAt());
        r.setUpdatedAt(doc.getUpdatedAt());
        return r;
    }
}
