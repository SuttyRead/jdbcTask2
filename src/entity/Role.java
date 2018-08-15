package entity;

public class Role {

    private java.lang.Long id;
    private String name;

    public Role(java.lang.Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {
    }

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
