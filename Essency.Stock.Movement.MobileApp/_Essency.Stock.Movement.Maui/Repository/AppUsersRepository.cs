using Essency.Stock.Movement.Maui.Data;
using Essency.Stock.Movement.Maui.Interfaces;

namespace Essency.Stock.Movement.Maui.Repository
{
    class AppUsersRepository : IAppUsers
    {
        private readonly AppUsersDb _db;

        public AppUsersRepository(AppUsersDb db) 
        {
            _db=db;
        }

        public async Task<bool> Login(string username, string password)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
                    throw new ArgumentException("Username and password cannot be empty.");

                bool login = await _db.LoginUser(username, password).ConfigureAwait(false);

                return login;
            }
            catch (Exception ex)
            {
                throw new ArgumentException($"Login error: {ex.Message}");
                throw;
            }
        }

    }
}
