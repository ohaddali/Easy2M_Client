package nok.easy2m;

import java.util.List;

/**
 * Created by naordalal on 20/02/2018.
 */

public class Contact
{

    private String id;
    private String name;
    private List<String> phoneNumbers;
    private List<String> emails;

    public Contact(String id, String name, List<String> phoneNumbers, List<String> emails)
    {
        this.id = id;
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

}
