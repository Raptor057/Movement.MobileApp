namespace Essency.Stock.Movement.Maui.Interfaces
{
    public interface IAppUsers
    {

        Task<bool> Login(string Username, string Password);

    }
}
