import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit {

  fullName = '';
  role = '';
  unreadCount = 0;
  isMobileMenuOpen = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.fullName = this.authService.getFullName();
    this.role = this.authService.getRole();
    this.loadUnreadCount();
  }

  loadUnreadCount(): void {
    this.notificationService.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
        this.cdr.markForCheck();
      }
    });
  }

  isBusinessUser(): boolean {
    return this.authService.isBusinessUser();
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    this.cdr.markForCheck();
  }

  logout(): void {
    this.authService.logout();
  }
}