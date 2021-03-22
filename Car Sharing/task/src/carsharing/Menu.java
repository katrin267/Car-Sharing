package carsharing;

import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private CompanyDatabase dataBase;


    Menu(String dbName) {
        dataBase = new CompanyDatabase(dbName);
    }

    void mainMenu() {
        while (true) {
            System.out.println();
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer ");
            System.out.println("0. Exit");
            switch (scanner.nextLine()) {
                case "1":
                    showSubMenu();
                    break;
                case "2":
                    boolean isEmpty = dataBase.showCustomerList();

                    if (!isEmpty) {
                        System.out.println("0. Back");
                        String customerId = scanner.nextLine();
                        if (customerId.equals("0")) {
                            break;
                        } else {
                            showCustomerMenu(customerId);
                        }
                    }
                    break;
                case "3":
                    System.out.println("Enter the customer name:");
                    dataBase.addCustomer(scanner.nextLine());
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    private void showCustomerMenu(String customerId) {
        while (true) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");
            switch (scanner.nextLine()) {
                case "1":
                    if (dataBase.isCarRented(customerId)) {
                        System.out.println("You've already rented a car!");
                        break;
                    }
                    boolean isEmpty = dataBase.showCompaniesList();

                    if (!isEmpty) {
                        System.out.println("0. Back");
                        String companyId = scanner.nextLine();
                        if (companyId.equals("0")) {
                            break;
                        } else {
                            List<Integer> indexList = dataBase.showCarsList(companyId);

                            if (indexList.size() > 0) {
                                System.out.println("0. Back");
                                System.out.println();
                                System.out.println("Choose a car:");
                                String carId = scanner.nextLine();
                                if (carId.equals("0")) {
                                    break;
                                }
                                dataBase.rentCar(indexList.get(Integer.parseInt(carId) - 1).toString(), customerId);
                            } else {
                                System.out.println("No available cars in the " + dataBase.getCompanyNameById(companyId) + " company");
                                break;
                            }
                        }
                    }
                    break;
                case "2":
                    dataBase.returnRentedCar(customerId);
                    break;
                case "3":
                    dataBase.showRentedCar(customerId);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    private void showSubMenu() {
        while (true) {
            System.out.println();
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");
            switch (scanner.nextLine()) {
                case "1":
                    boolean isEmpty = dataBase.showCompaniesList();
                    System.out.println("0. Back");

                    if (!isEmpty) {
                        String companyId = scanner.nextLine();
                        if (companyId.equals("0")) {
                            break;
                        } else {
                            showCarMenu(companyId);
                        }
                    }
                    break;
                case "2":
                    System.out.println("Enter the company name:");
                    dataBase.addCompany(scanner.nextLine());
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    private void showCarMenu(String companyId) {
        String companyName = dataBase.getCompanyNameById(companyId);
        while (true) {
            System.out.println();
            System.out.println("'" + companyName + "' company:");
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            switch (scanner.nextLine()) {
                case "1":
                    if (dataBase.showCarsList(companyId).size() == 0) {
                        System.out.println("The car list is empty!");
                    }
                    break;
                case "2":
                    System.out.println("Enter the car name:");
                    dataBase.addCar(scanner.nextLine(), companyId);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }
}
