package com.example.demo.service;

import com.example.demo.entity.Result;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Duration;
import java.util.List;

@Service
public class AutomationService {
    @Autowired
    private ExcelService excelService;

    private WebDriver driver;

    public Result processExcelAndCancelOrders(MultipartFile file) {
        try {
            List<String> codes = excelService.readCodes(file.getInputStream());
            WebDriver webDriver = setupDriver();
            return processCodes(codes, webDriver);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý file hoặc selenium: " + e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private WebDriver setupDriver() {
        WebDriverManager.chromedriver().setup(); // tự tải chromedriver phù hợp
        return new ChromeDriver();
    }

    private Result processCodes(List<String> codes, WebDriver driver) throws InterruptedException {
        Result result = new Result();
        driver.get("https://bpm.mic.vn/payment/settlement-contract-terminate/search");

        Thread.sleep(2000);
        WebElement usernameField = driver.findElement(By.cssSelector("input[placeholder='Tài khoản']"));
        usernameField.sendKeys("hieupd04@mic.vn");

        WebElement passwordField = driver.findElement(By.cssSelector("input[placeholder='Mật khẩu']"));
        passwordField.sendKeys("Pdh13998@");

        WebElement loginButton = driver.findElement(By.xpath("//button[span//text()='Đăng nhập']"));
        loginButton.click();

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Actions actions = new Actions(driver);

        WebElement menuLv1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[contains(@class, 'menu-lv1')]//span[@class='wrap-text' and contains(text(), 'Thanh toán hợp đồng')]")));


        actions.moveToElement(menuLv1).perform();

        WebElement menuLv2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), 'Chấm dứt hợp đồng')]")
        ));
        actions.moveToElement(menuLv2).perform();

        WebElement menuLv3 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), 'Thực hiện chấm dứt hợp đồng')]")
        ));
        menuLv3.click();

        for (String code : codes) {
            WebElement clearButton = driver.findElement(By.id("el-id-7091-98"));
            clearButton.click();

            WebElement searchBox = driver.findElement(By.id("el-id-7091-30"));
            searchBox.clear();
            searchBox.sendKeys(code);
            searchBox.sendKeys(Keys.RETURN);
            Thread.sleep(2000);

            String xpath = "//span[@class='show-overflow-tooltip el-tooltip__trigger el-tooltip__trigger' and text()='" + code + "']";

            List<WebElement> spans = driver.findElements(By.xpath(xpath));

            if (spans.isEmpty()) {
                System.out.println("Không tìm thấy mã: " + code);
                continue;
            }
            WebElement detailLink = driver.findElement(By.cssSelector(".order-detail-link"));
            detailLink.click();
            Thread.sleep(2000);

            Select selectTab = new Select(driver.findElement(By.id("tab-TAB3")));
            selectTab.selectByVisibleText("Hủy đơn");

            WebElement uploadButton = driver.findElement(By.xpath("//div[text()='Tải tài liệu lên']"));
            uploadButton.click();

            WebElement upload = driver.findElement(By.id("uploadFile"));
            upload.sendKeys(new File("src/main/resources/static/huy.pdf").getAbsolutePath());
            Thread.sleep(1000);

            WebElement cancelButton = driver.findElement(By.id("cancelButton"));
            cancelButton.click();
            Thread.sleep(3000);

            driver.get("https://your.website.com/search-page");
            Thread.sleep(2000);
        }

        return result;
    }
}
