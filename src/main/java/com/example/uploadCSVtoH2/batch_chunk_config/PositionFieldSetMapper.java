package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.PhoneCall;
import com.example.uploadCSVtoH2.entity.Position;
import com.example.uploadCSVtoH2.repository.TextMessageRepository;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PositionFieldSetMapper implements FieldSetMapper<Position> {

    @Autowired
    TextMessageRepository textMessageRepository;

    @Override
    public Position mapFieldSet(FieldSet fieldSet) {

        Position position = new Position();
        position.setPositionId(Long.parseLong(fieldSet.readString("positionId")));
        position.setLatitudine(Long.parseLong(fieldSet.readString("latitude")));
        position.setLongitudine(Long.parseLong(fieldSet.readString("longitude")));

        return position;
    }
}
