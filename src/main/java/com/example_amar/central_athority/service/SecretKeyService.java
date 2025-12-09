package com.example_amar.central_athority.service;

import com.example_amar.central_athority.exceptions.ResourceNotFoundException;
import com.example_amar.central_athority.model.Installer;
import com.example_amar.central_athority.repository.InstallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecretKeyService {
    private static final String HMAC_algo="HmacSHA256";

    private  final InstallerRepository installerRepository ;
    private String getSharedSecret(String installer_uuid)
    {
        try{
            UUID uuid;
            uuid=UUID.fromString(installer_uuid);
            Optional<Installer> installerOptional=installerRepository.findByInstallerUuid(uuid);

            if(installerOptional.isEmpty())
            {
                throw new ResourceNotFoundException("Installer not found");
            }
            Installer installer=installerOptional.get();

            if(installer.getIsActive()==null|| !installer.getIsActive())
            {
                throw new SecurityException("Installer is not active");
            }

            return installer.getInstallerSecret();

        }catch(Exception e){
            return null;
        }
    }

    public boolean verifyHmac(String installerUUID,String appId,String deviceId,String installerHmacCode)
    {
        try{
                String sharedSecret=getSharedSecret(installerUUID);
                byte[] receivedMac;
                try{
                     receivedMac=Base64.getDecoder().decode(installerHmacCode);
                }catch(Exception e){
                    return false;
                }
                String SignedData=appId+":"+deviceId; /// signed data has : between dont forget............
                byte[] keyBytes=sharedSecret.getBytes(StandardCharsets.UTF_8);
                byte[] dataBytes=SignedData.getBytes(StandardCharsets.UTF_8);

                // calculating hmac to comapare with client value
            SecretKeySpec secretKeySpec=new SecretKeySpec(keyBytes,HMAC_algo);
            Mac mac= Mac.getInstance(HMAC_algo);
            mac.init(secretKeySpec);
            byte[] serverCalcMac=mac.doFinal(dataBytes);
            return MessageDigest.isEqual(receivedMac,serverCalcMac);

        }catch (Exception e )
        {
            return false;
        }
    }




}
