package model;

import model.input.WeatherManager;
import model.object.Email;
import model.output.EmailManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ModelFacadeImplTest {
    private ModelFacade modelFacade;
    private WeatherManager weatherManager;
    private EmailManager emailManager;

    @BeforeEach
    void setup() {
        weatherManager = mock(WeatherManager.class);
        emailManager = mock(EmailManager.class);
        modelFacade = new ModelFacadeImpl(weatherManager, emailManager);
    }

    @Test
    void sendReport() {
        Email email = mock(Email.class);
        when(emailManager.styleReport(anyString(), anyList())).thenReturn(email);
        when(emailManager.sendReport(email)).thenReturn(true);

        assertThat(modelFacade.sendReport("test@gmail.com"), is(true));

        verify(emailManager, times(1)).sendReport(email);
    }
}
