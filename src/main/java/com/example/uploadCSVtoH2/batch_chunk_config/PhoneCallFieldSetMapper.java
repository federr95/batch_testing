package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.PhoneCall;
import com.example.uploadCSVtoH2.entity.Position;
import com.example.uploadCSVtoH2.repository.PhoneCallRepository;
import com.example.uploadCSVtoH2.repository.PositionRepository;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PhoneCallFieldSetMapper implements FieldSetMapper<PhoneCall> {

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    PhoneCallRepository phoneCallRepository;

    @Override
    public PhoneCall mapFieldSet(FieldSet fieldSet) {

        Long phoneCallId = Long.parseLong(fieldSet.readString("phoneCallId"));

        Optional<PhoneCall> phoneCallObj = Optional.of(phoneCallRepository.getById(phoneCallId));

        if(phoneCallObj.isPresent()){
            // se l'oggetto phone call è già presente (con solamente l'id) perchè inserito da parte di qualche altro csv
            // aggiungo gli elementi che mancano a comletare l'oggetto. Lavoro quindi su phoneCallObj e lo ritorno

            return phoneCallObj.get();
        } else {
            final PhoneCall phoneCall = new PhoneCall();
            phoneCall.setPhoneCallId(Long.parseLong(fieldSet.readString("phoneCallId")));
            phoneCall.setPhoneCallReceiver(Long.parseLong(fieldSet.readString("phoneCallReceiver")));
            phoneCall.setPhoneCallSender(Long.parseLong(fieldSet.readString("phoneCallSender")));
            phoneCall.setDuration(Long.parseLong(fieldSet.readString("duration")));
            // bisogna controllare la stessa cosa per la posizione, ovvero andare a vedere se l'oggetto posizione con
            // quel determinato id è presente in modo completo oppure no e nel caso creare un nuovo oggetto oppure completare
            //
            long positionId = Long.parseLong(fieldSet.readString("position"));
            Position position = new Position(positionId);
            phoneCall.setPosition(position);
            return phoneCall;
        }

    }
}
