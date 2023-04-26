import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScheduleSeleniumTest {

    private static WebDriver driver;
    private static String url = "http://localhost:8080/app/users";
    private static String userName;
    private static String name;
    private static String password;

    @BeforeAll
    public static void setup(){
        WebDriverManager.chromedriver().setup();
        driver =  new ChromeDriver( new ChromeOptions().addArguments("--remote-allow-origins=*"));
    }

    @AfterAll
    public static void destroy(){
        driver.quit();
    }

    public void gerarDadosCadastroUsuario(){
        this.name = RandomStringUtils.randomAlphabetic(20);
        this.userName = RandomStringUtils.randomAlphabetic(20);
        this.password = RandomStringUtils.random(10, true, true);
    }

    @Test
    @Order(1)
    public void cadastrarUsuario_suceso(){
        driver.get(url);
        gerarDadosCadastroUsuario();
        driver.findElement(By.className("create")).click();
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.xpath("//*[text()='" + userName + "']"));

        assertEquals(userName, element.getText());
    }

    @Test
    public void cadastrarUsuarioSemNome_falha(){
        driver.get(url);
        gerarDadosCadastroUsuario();
        driver.findElement(By.className("create")).click();
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));

        assertEquals("não deve estar em branco", element.getText());
    }

    @Test
    public void cadastrarUsuarioSemUsername_falha(){
        driver.get(url);
        gerarDadosCadastroUsuario();
        driver.findElement(By.className("create")).click();
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));

        assertEquals("não deve estar em branco", element.getText());
    }

    @Test
    public void cadastrarUsuarioSemPassword_falha(){
        driver.get(url);
        driver.findElement(By.className("create")).click();
        gerarDadosCadastroUsuario();
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));

        assertEquals("não deve estar em branco", element.getText());
    }

    @Test
    @Order(2)
    public void cadastrarUsuarioUserNameJaCadastrado_falaha(){
        driver.get(url);
        driver.findElement(By.className("create")).click();
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));

        assertEquals("Username already in use", element.getText());
    }

    @Test
    @Order(3)
    public void atualizarNameAndPasswordUsuario_sucesso(){
        driver.get(url);
        driver.findElement( By.xpath("//*[text()='" + userName + "']/following-sibling::td/a[text()='Edit']")).click();
        String novoNome = "novoNome";
        String novaSenha = "123456";
        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("name")).sendKeys(novoNome);
        driver.findElement(By.id("password")).sendKeys(novaSenha);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement( By.xpath("//*[text()='" + novoNome + "']"));
        assertNotNull(element);
    }

    @Test
    @Order(3)
    public void atualizarUserName_falha(){
        driver.get(url);
        driver.findElement( By.xpath("//*[text()='" + userName + "']/following-sibling::td/a[text()='Edit']")).click();
        String novoUserName = "novoUsername";
        WebElement element = driver.findElement(By.id("username"));
        assertThrows(InvalidElementStateException.class, () -> element.clear());
        element.sendKeys(novoUserName);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-default")).click();
        assertNotNull(driver.findElement( By.xpath("//*[text()='" + userName + "']")));
    }

    @Test
    @Order(3)
    public void atualizarUsuarioSemName_sucesso(){
        driver.get(url);
        driver.findElement( By.xpath("//*[text()='" + userName + "']/following-sibling::td/a[text()='Edit']")).click();
        String novaSenha = "123456";
        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("password")).sendKeys(novaSenha);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));
        assertEquals("não deve estar em branco", element.getText());
    }

    @Test
    @Order(3)
    public void atualizarUsuarioSemPassword_falha(){
        driver.get(url);
        driver.findElement( By.xpath("//*[text()='" + userName + "']/following-sibling::td/a[text()='Edit']")).click();
        String novoName = "novoName";

        WebElement elementoNome = driver.findElement(By.id("name"));
        elementoNome.clear();
        elementoNome.sendKeys(novoName);
        driver.findElement(By.className("btn-default")).click();
        WebElement element = driver.findElement(By.className("user-form-error"));
        assertEquals("não deve estar em branco", element.getText());
    }

    @Test
    @Order(3)
    public void atualizar_SenhaNaoDeveSerExibida(){
        driver.get(url);
        driver.findElement( By.xpath("//*[text()='" + userName + "']/following-sibling::td/a[text()='Edit']")).click();
        WebElement elementoNome = driver.findElement(By.id("password"));
        assertEquals("", elementoNome.getText());
    }



}
