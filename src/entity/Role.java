package entity;

public class Role {

    private Long id;
    private String Name;

    public Role(Long id, String name) {
        this.id = id;
        Name = name;
    }

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", Name='" + Name + '\'' +
                '}';
    }

}
