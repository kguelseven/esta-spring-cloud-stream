package ch.sbb.esta.scs.componenttest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MyTest {

    @Test
    public void sampleTest() {
        //        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
        //                TestChannelBinderConfiguration.getCompleteConfiguration(
        //                        MessagingConfiguration.class))
        //                .run("--spring.cloud.function.definition=uppercase")) {
        //            InputDestination source = context.getBean(InputDestination.class);
        //            OutputDestination target = context.getBean(OutputDestination.class);
        //            source.send(new GenericMessage<byte[]>("hello".getBytes()));
        //            assertThat(target.receive().getPayload()).isEqualTo("HELLO".getBytes());
        //        }
    }

}
