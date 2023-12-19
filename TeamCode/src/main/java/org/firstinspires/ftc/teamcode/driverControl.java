package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name = "RegularDrive", group = "Match")
public class driverControl extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("FL");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("BL");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("FR");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("BR");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = -gamepad1.right_stick_x;


            //direction reverser, choose either based on driver preference
            if(gamepad1.left_bumper){
                y = -y;
                x = -x;
            }

            /*if(gamepad1.left_stick_button){
                y = -y;
                x = -x;
            }*/


            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);


            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower / (2 - gamepad1.left_trigger)); //triggers give an analog input from 0-1
            backLeftMotor.setPower(backLeftPower / (2 - gamepad1.left_trigger)); //depending on the amount the trigger is pressed
            frontRightMotor.setPower(frontRightPower / (2 - gamepad1.left_trigger)); //the amount of acceleration varies
            backRightMotor.setPower(backRightPower / (2 - gamepad1.left_trigger)); //by fully pressing down, you are using the robot's full power

            telemetry.addData("Control Value", gamepad1.left_bumper);
            telemetry.update();
            
        }
    }
}

//below here is the experimental centric drive, this is not operational
/*
@TeleOp(name = "FieldCentricDrive", group = "Match")
public class driverControl extends LinearOpMode {

    DcMotor frontRightMotor;
    DcMotor frontLeftMotor;
    DcMotor backRightMotor;
    DcMotor backLeftMotor;



    @Override
    public void runOpMode() throws InterruptedException {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "FL");
        frontRightMotor = hardwareMap.get(DcMotor.class, "FR");
        backRightMotor = hardwareMap.get(DcMotor.class, "BR");
        backLeftMotor = hardwareMap.get(DcMotor.class, "BL");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
        RevHubOrientationOnRobot.LogoFacingDirection.UP,
        RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower/5);
            backLeftMotor.setPower(backLeftPower/5);
            frontRightMotor.setPower(frontRightPower/5);
            backRightMotor.setPower(backRightPower/5);
        }
    }
}


    private void controlWheels() {
        // enable for both controller
        double y, x , rx, denominator;
        double frontLeftPower, backLeftPower, frontRightPower, backRightPower, speedmultiplier;
        y = gamepad1.left_stick_y * 0.5; // Remember, this is reversed!
        x = -gamepad1.left_stick_x * 1.1 * 0.5; // Counteract imperfect strafing
        rx = -gamepad1.right_stick_x * 0.5;

        //if (gamepad1.left_stick_x != 0 || gamepad1.right_stick_x != 0) {
        y = gamepad1.left_stick_y * 0.5; // Remember, this is reversed!
        x = -gamepad1.left_stick_x * 1.1 * 0.5; // Counteract imperfect strafing
        rx = -gamepad1.right_stick_x * 0.5;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        frontLeftPower = (y + x + rx) / denominator; //y + x + rx
        backLeftPower = (y - x + rx) / denominator; //y - x + rx
        frontRightPower = (y - x - rx) / denominator; //y - x - rx
        backRightPower = (y + x - rx) / denominator;
        speedmultiplier = 1;
        if (gamepad1.left_trigger > 0) {
            speedmultiplier = 0.2;
        } else if (gamepad1.right_trigger > 0) {
            speedmultiplier = 2;
        } else {
            speedmultiplier = 1;
        }

        fl.setPower(frontLeftPower * speedmultiplier);
        bl.setPower(backLeftPower * speedmultiplier);
        fr.setPower(frontRightPower * speedmultiplier);
        br.setPower(backRightPower * speedmultiplier);

        //telemetry.addData("FLSpd", frontLeftPower * speedmultiplier);
        //telemetry.addData("RRSpd", backRightPower * speedmultiplier);
        //telemetry.update();

    }

}

*/