import { HttpInterceptorFn } from '@angular/common/http';
 
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // get token from localStorage
  const token = localStorage.getItem('token');
 
  // if token exists add it to every request automatically
  if (token) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(cloned);
  }
 
  return next(req);
};
 