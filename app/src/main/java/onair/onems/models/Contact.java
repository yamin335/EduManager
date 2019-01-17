package onair.onems.models;


public class Contact {
    private String name;
    private String image;
    private String phone = "01721847998";

    public Contact() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }
}
