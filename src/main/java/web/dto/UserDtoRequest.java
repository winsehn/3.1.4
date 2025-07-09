package web.dto;


public class UserDtoRequest {
    private UserDto userDto;
    private String password;


    public UserDtoRequest(UserDto userDto, String password) {
        this.userDto = userDto;
        this.password = password;
    }

    public UserDtoRequest() {
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
